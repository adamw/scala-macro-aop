package com.softwaremill.aop

import scala.reflect.macros.Context

object delegateMacro {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    def reportInvalidAnnotationTarget() {
      c.error(c.enclosingPosition, "This annotation can only be used on vals")
    }

    // From MacWire ...
    def typeCheckExpressionOfType(typeTree: Tree): Type = {
      val someValueOfTypeString = reify {
        def x[T](): T = throw new Exception
        x[String]()
      }

      val Expr(Block(stats, Apply(TypeApply(someValueFun, _), someTypeArgs))) = someValueOfTypeString

      val someValueOfGivenType = Block(stats, Apply(TypeApply(someValueFun, List(typeTree)), someTypeArgs))
      val someValueOfGivenTypeChecked = c.typeCheck(someValueOfGivenType)

      someValueOfGivenTypeChecked.tpe
    }

    def computeType(tpt: Tree): Type = {
      if (tpt.tpe != null) {
        tpt.tpe
      } else {
        val calculatedType = c.typeCheck(tpt.duplicate, silent = true, withMacrosDisabled = true).tpe
        val result = if (tpt.tpe == null) calculatedType else tpt.tpe

        if (result == NoType) {
          typeCheckExpressionOfType(tpt)
        } else {
          result
        }
      }
    }
    // ... until here

    def addDelegateMethods(valDef: ValDef, addToClass: ClassDef) = {
      def allMethodsInDelegate = computeType(valDef.tpt).declarations

      val ClassDef(mods, name, tparams, Template(parents, self, body)) = addToClass

      // TODO better filtering - allow overriding
      val existingMethods = body.flatMap(tree => tree match {
        case DefDef(_, n, _, _, _, _) => Some(n)
        case _ => None
      }).toSet
      val methodsToAdd = allMethodsInDelegate.filter(method => !existingMethods.contains(method.name))

      val newMethods = for {
        methodToAdd <- methodsToAdd
      } yield {
        val methodSymbol = methodToAdd.asMethod

        val vparamss = methodSymbol.paramss.map(_.map {
          paramSymbol => ValDef(
            Modifiers(Flag.PARAM, tpnme.EMPTY, List()),
            paramSymbol.name.toTermName,
            TypeTree(paramSymbol.typeSignature),
            EmptyTree)
        })

        val delegateInvocation = Apply(
          Select(Ident(valDef.name), methodSymbol.name),
          methodSymbol.paramss.flatMap(_.map(param => Ident(param.name)))) // TODO - multi params list

        DefDef(Modifiers(),
          methodSymbol.name,
          List(), // TODO - type parameters
          vparamss,
          TypeTree(methodSymbol.returnType),
          delegateInvocation)
      }

      ClassDef(mods, name, tparams, Template(parents, self, body ++ newMethods))
    }

    val inputs = annottees.map(_.tree).toList
    val (_, expandees) = inputs match {
      case (param: ValDef) :: (enclosing: ClassDef) :: rest => {
        val newEnclosing = addDelegateMethods(param, enclosing)
        (param, newEnclosing :: rest)
      }
      case (param: TypeDef) :: (rest @ (_ :: _)) => reportInvalidAnnotationTarget(); (param, rest)
      case _ => reportInvalidAnnotationTarget(); (EmptyTree, inputs)
    }
    val outputs = expandees
    c.Expr[Any](Block(outputs, Literal(Constant(()))))
  }
}
