package br.com.zup.ot6.izabel

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("br.com.zup.ot6.izabel")
		.start()
}

