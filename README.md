scala-macro-aop
===============

delegate / proxy / decorator
----------------------------

The `@delegate` macro annotation can be applied to class parameters. The macro will generate missing methods which
delegate calls to the annotated value.

Currently the implementation is at a POC stage, doesn't support overloading, method type parameters or multiple
parameter lists.

For example:

````scala
trait Foo {
  def method1(): String
  def method2(p1: String): Long
  def method3(p1: String): Int
  def method4(p1: String, p2: Long): String
}

class FooImpl extends Foo {
  def method1() = "m1"
  def method2(p1: String) = 42L
  def method3(p1: String) = p1.length()
  def method4(p1: String, p2: Long) = s"ok $p1 $p2"
}

class FooWrapper(@delegate wrapped: Foo) extends Foo {
  def method2(p1: String) = 41L
}
````

This will compile, even though there's no direct definition of `method1`, `method3` or `method4` in `FooWrapper`.
That's because of the `@delegate` macro annotation, which will generate the missing methods (`method2` is already
defined). The generated code will be, for example:

````scala
def method4(p1: String, p2: Long) = wrapped.method4(p2, p3)
````

To see the macro in action, execute `sbt run`.

