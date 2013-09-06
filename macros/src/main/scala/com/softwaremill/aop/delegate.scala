package com.softwaremill.aop

import scala.language.experimental.macros

import scala.annotation.StaticAnnotation

class delegate extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro delegateMacro.impl
}
