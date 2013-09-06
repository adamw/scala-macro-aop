scala-macro-aop
===============

delegate / proxy / decorator
----------------------------

The `@delegate` macro annotation can be applied to class parameters. The macro will generate missing methods which
delegate calls to the annotated value.

Currently the implementation is at a POC stage, doesn't support overriding, method type parameters or multiple
parameter lists.

For example:

````scala
trait Foo {
    def m1(p1: String): Int
    def m2(p2: Long, p3: Float): String
}

class FooImpl extends Foo {
    def m1(p1: String) = 0
    def m2(p2: Long, p3: Float) = "0"
}

class FooWrapper(@delegate wrapped: Foo) extends Foo {
    def m1(p1: String) = 1
}
````

This will compile, even though there's no direct definition of `m2` in `FooWrapper`. That's because of the `@delegate`
macro annotation, which will generate the `m2` method (`m1` is already defined). The generated code will be:

````scala
def m2(p2: Long, p3: Float) = wrapped.m2(p2, p3)
````

To see the macro in action, execute `sbt run`.

