<h1 align="center">𝐻𝒾 𝓉𝒽𝑒𝓇𝑒, 𝐼'𝓂 <a href="https://t.me/Cocosik1558" target="_blank">Нгуен Зуй-Ань Куеевич</a> 
<img src="https://github.com/blackcater/blackcater/raw/main/images/Hi.gif" height="32"/></h1>
<h3 align="center">A second-year student of the Faculty of Computer Science at SibSUTIS.</h3>
<h3 align="center">My group IKS-433</h3>
<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&pause=1000&width=435&lines=We+are+making+the+future+better." alt="Typing SVG" />

# Отчет по программе Интерфес

Содержание папки src:
1. [Movable.kt](https://github.com/Darkness1853/Android-Project/blob/bc7a24f627e97741491a0ab15651b07f4f20cc0f/src/Movable.kt)
2. [Human.kt](https://github.com/Darkness1853/Android-Project/blob/bc7a24f627e97741491a0ab15651b07f4f20cc0f/src/Human.kt)
3. [Driver.kt](https://github.com/Darkness1853/Android-Project/blob/bc7a24f627e97741491a0ab15651b07f4f20cc0f/src/Driver.kt)
4. [Main.kt](https://github.com/Darkness1853/Android-Project/blob/bc7a24f627e97741491a0ab15651b07f4f20cc0f/src/main.kt)
## Общее описание программы

Программа моделирует движение людей и водителей в двумерном пространстве с использованием объектно-ориентированного программирования, наследования и многопоточности.

## Архитектура программы

### 1. Интерфейс Movable

```kotlin
interface Movable {
    var x: Int
    var y: Int
    var speed: Int
    fun move()
}
```

**Назначение:** Интерфейс определяет контракт для всех движущихся объектов.

**Свойства:**
- `x`, `y` - координаты положения объекта в пространстве
- `speed` - скорость движения объекта

**Методы:**
- `move()` - метод для реализации движения

### 2. Базовый класс Human

```kotlin
open class Human(
    var name: String,
    var surname: String,
    var age: Int
) : Movable
```

**Назначение:** Базовый класс, представляющий человека, реализующий интерфейс Movable.

**Конструктор:**
- Принимает параметры: имя, фамилию, возраст
- Автоматически инициализирует свойства класса

**Свойства (реализация интерфейса Movable):**
```kotlin
override var x: Int = 0
override var y: Int = 0
override var speed: Int = 1
```

**Инициализатор:**
```kotlin
init {
    println("Создан человек: $name")
}
```
**Назначение:** Выполняется при создании объекта, выводит сообщение о создании человека.

### 3. Метод move() класса Human

```kotlin
override fun move() {
    x += Random.nextInt(-speed, speed + 1)
    y += Random.nextInt(-speed, speed + 1)
    println("$name переместился в ($x, $y)")
}
```

**Алгоритм работы:**
1. **Генерация случайного движения:**
   - `Random.nextInt(-speed, speed + 1)` генерирует случайное число от -speed до speed
   - Для скорости 1: возможные значения -1, 0, 1

2. **Обновление координат:**
   - `x += случайное_смещение` - изменяет координату X
   - `y += случайное_смещение` - изменяет координату Y

3. **Вывод информации:**
   - Печатает имя и новые координаты

**Математическая формула движения:**
```
xₙ = xₙ₋₁ + Δx, где Δx ∈ [-speed, speed]
yₙ = yₙ₋₁ + Δy, где Δy ∈ [-speed, speed]
```

### 4. Класс Driver (наследник Human)

```kotlin
class Driver(
    name: String,
    surname: String,
    age: Int,
    var carModel: String = "Toyota"
) : Human(name, surname, age)
```

**Назначение:** Специализированный класс, представляющий водителя с автомобилем.

**Наследование:**
- Наследует от класса Human все свойства и методы
- Добавляет новое свойство `carModel`

**Инициализатор:**
```kotlin
init {
    speed = 2
    println("Создан водитель: $name")
}
```
**Назначение:** Устанавливает скорость водителя в 2 единицы и выводит сообщение.

### 5. Переопределенный метод move() в Driver

```kotlin
override fun move() {
    x += speed
    println("Водитель $name проехал на $carModel в ($x, $y)")
}
```

**Особенности по сравнению с Human:**
- **Движение только по оси X:** `x += speed`
- **Постоянная скорость:** Всегда движется вперед со скоростью 2
- **Информация об автомобиле:** В выводе указывается модель машины

**Математическая формула:**
```
xₙ = xₙ₋₁ + speed
yₙ = yₙ₋₁ (не изменяется)
```

### 6. Главная функция main()

```kotlin
fun main() {
    // Шаг 1: Создание объектов
    val human1 = Human("Анна", "Иванова", 20)
    val human2 = Human("Иван", "Петров", 25)
    val human3 = Human("Мария", "Сидорова", 22)
    val driver = Driver("Алексей", "Кузнецов", 30)
    
    // Шаг 2: Ввод времени 
    println("Сколько секунд двигаться?")
    val time = readLine()?.toIntOrNull() ?: 0
    if (time <= 0) return
    println("Начинаем движение!")
    
    // Шаг 3: Цикл 
    for (second in 1..time) {
        println("\nСекунда $second")
        
        // Шаг 4: Создание потоков для каждого объекта
        val thread1 = thread { human1.move() }
        val thread2 = thread { human2.move() }
        val thread3 = thread { human3.move() }
        val thread4 = thread { driver.move() } 
        
        // Шаг 5: Ожидание завершения всех потоков
        thread1.join()
        thread2.join()
        thread3.join()
        thread4.join()
    }
    
    // Шаг 6: Вывод финальных результатов
    println("\nРезультаты")
    println("${human1.name}: (${human1.x}, ${human1.y})")
    println("${human2.name}: (${human2.x}, ${human2.y})")
    println("${human3.name}: (${human3.x}, ${human3.y})")
    println("${driver.name}: (${driver.x}, ${driver.y})")
}
```

