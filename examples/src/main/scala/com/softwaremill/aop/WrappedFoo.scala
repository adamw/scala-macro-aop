package com.softwaremill.aop

class WrappedFoo(@delegate d: Foo) extends Foo {
  def method3(p1: String) = {
    println(s"ALT method3 $p1")
    41
  }
}
