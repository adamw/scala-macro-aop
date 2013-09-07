package com.softwaremill.aop

class FooWrapper(@delegate wrapped: Foo) extends Foo {
  def method2(p1: String) = 41L
}
