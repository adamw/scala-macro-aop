package com.softwaremill.aop

class DefaultFoo extends Foo {
  def method1() = {
    "m1"
  }

  def method2(p1: String) = {
    91012L
  }

  def method3(p1: String) = {
    42
  }

  def method4(p1: String, p2: Long) = {
    s"ok $p1 $p2"
  }
}
