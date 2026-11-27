// v7 check: kotlinc 2.4.20 still emits this$0 for an inner class that never uses it (javap -p A$B)
class A { inner class B { fun f() = 1 } }
