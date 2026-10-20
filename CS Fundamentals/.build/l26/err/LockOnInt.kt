var boxed: Int? = 0                     // a nullable Int is a boxed Integer object

fun bump() {
    synchronized(boxed!!) {             // lock on the counter's own box...
        boxed = boxed!! + 1             // ...which this line replaces with a new box
    }
}
