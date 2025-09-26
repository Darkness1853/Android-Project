class Driver(
    name: String,
    surname: String,
    age: Int,
    var carModel: String = "Toyota"
) : Human(name, surname, age) {
    
    init {
        speed = 2
        println("Создан водитель: $name")
    }
    
    override fun move() {
        x += speed
        println("Водитель $name проехал на $carModel в ($x, $y)")
    }
}
