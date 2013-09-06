package com.softwaremill.aop

object Usage extends App {
  val f = new DefaultFoo
  val w = new WrappedFoo(f)

  println("Method 1 result: " + w.method1())
  println("Method 2 result: " + w.method2("x"))
  println("Method 3 result: " + w.method3("y"))
  println("Method 4 result: " + w.method4("z", 10L))
}
