import kotlin.random.Random

open class Human(
    var name: String,
    var surname: String,
    var age: Int
) : Movable {
    override var x: Int = 0
    override var y: Int = 0
    override var speed: Int = 1

    init {
        println("Создан человек: $name")
    }

    override fun move() {
        x += Random.nextInt(-speed, speed + 1)
        y += Random.nextInt(-speed, speed + 1)
        println("$name переместился в ($x, $y)")
    }
}
