package com.softwaremill.aop

object Usage extends App {
  def invokeMethodsOnFoo(foo: Foo) {
    println("Method 1 result: " + foo.method1())
    println("Method 2 result: " + foo.method2("x"))
    println("Method 3 result: " + foo.method3("y"))
    println("Method 4 result: " + foo.method4("z", 10L))
  }

  val original = new FooImpl
  val wrapped = new FooWrapper(original)

  println("Original:")
  invokeMethodsOnFoo(original)

  println("---------")
  println("Wrapped:")
  invokeMethodsOnFoo(wrapped)
}
