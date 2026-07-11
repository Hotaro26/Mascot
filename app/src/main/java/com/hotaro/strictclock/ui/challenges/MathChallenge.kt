package com.hotaro.strictclock.ui.challenges

import kotlin.random.Random

data class MathProblem(val expression: String, val answer: Int)

object MathChallenge {
    fun generateProblem(operationsStr: String, difficulty: String): MathProblem {
        val ops = if (operationsStr.isEmpty()) listOf("Addition") else operationsStr.split(",")
        val op = ops[Random.nextInt(ops.size)]
        
        var a = 0
        var b = 0
        var answer = 0
        var expr = ""

        when (op) {
            "Addition" -> {
                when (difficulty) {
                    "Easy" -> { a = Random.nextInt(1, 20); b = Random.nextInt(1, 20) }
                    "Medium" -> { a = Random.nextInt(10, 100); b = Random.nextInt(10, 100) }
                    "Hard" -> { a = Random.nextInt(100, 1000); b = Random.nextInt(100, 1000) }
                    else -> { a = Random.nextInt(1, 20); b = Random.nextInt(1, 20) }
                }
                answer = a + b
                expr = "$a + $b"
            }
            "Subtraction" -> {
                when (difficulty) {
                    "Easy" -> { a = Random.nextInt(10, 30); b = Random.nextInt(1, a) }
                    "Medium" -> { a = Random.nextInt(30, 200); b = Random.nextInt(10, a) }
                    "Hard" -> { a = Random.nextInt(200, 1000); b = Random.nextInt(50, a) }
                    else -> { a = Random.nextInt(10, 30); b = Random.nextInt(1, a) }
                }
                answer = a - b
                expr = "$a - $b"
            }
            "Multiplication" -> {
                when (difficulty) {
                    "Easy" -> { a = Random.nextInt(2, 10); b = Random.nextInt(2, 10) }
                    "Medium" -> { a = Random.nextInt(10, 30); b = Random.nextInt(2, 10) }
                    "Hard" -> { a = Random.nextInt(20, 100); b = Random.nextInt(10, 30) }
                    else -> { a = Random.nextInt(2, 10); b = Random.nextInt(2, 10) }
                }
                answer = a * b
                expr = "$a × $b"
            }
            "Division" -> {
                when (difficulty) {
                    "Easy" -> { b = Random.nextInt(2, 10); answer = Random.nextInt(2, 10); a = b * answer }
                    "Medium" -> { b = Random.nextInt(2, 20); answer = Random.nextInt(5, 30); a = b * answer }
                    "Hard" -> { b = Random.nextInt(10, 50); answer = Random.nextInt(10, 50); a = b * answer }
                    else -> { b = Random.nextInt(2, 10); answer = Random.nextInt(2, 10); a = b * answer }
                }
                expr = "$a ÷ $b"
            }
            else -> {
                a = Random.nextInt(1, 10)
                b = Random.nextInt(1, 10)
                answer = a + b
                expr = "$a + $b"
            }
        }
        return MathProblem(expr, answer)
    }
}
