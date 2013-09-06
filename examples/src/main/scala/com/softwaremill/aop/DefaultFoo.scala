package com.softwaremill.aop

class DefaultFoo extends Foo {
  def method1() {
    println("method1")
  }

  def method2(p1: String) {
    println(s"method2 $p1")
  }

  def method3(p1: String) = {
    println(s"method3 $p1")
    42
  }

  def method4(p1: String, p2: Long) = {
    println(s"method4 $p1 $p2")
    "ok"
  }
}
