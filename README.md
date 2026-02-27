<h1 align="center">𝐻𝒾 𝓉𝒽𝑒𝓇𝑒, 𝐼'𝓂 <a href="https://t.me/Cocosik1558" target="_blank">Нгуен Зуй-Ань Куеевич</a> 
<img src="https://github.com/blackcater/blackcater/raw/main/images/Hi.gif" height="32"/></h1>
<h3 align="center">A second-year student of the Faculty of Computer Science at SibSUTIS.</h3>
<h3 align="center">My group IKS-433</h3>
<img src="https://readme-typing-svg.demolab.com?font=Fira+Code&pause=1000&width=435&lines=We+are+making+the+future+better." alt="Typing SVG" />

![GIF](https://github.com/Darkness1853/Pictures/blob/main/R%20(1).gif)

***Ответы на вопросы***

1. Зачем в конструкторе писать (var или val "имя переменной" : "Тип данных")

   Если мы объявим параметр в конструкторе просто без var или val, он будет доступен только для инициализации объекта.
   Мы не сможете обращаться к нему после создания экземпляра.
   Без val/var: параметр является просто аргументом для передачи в конструктор, но не свойством класса.

   val автоматически создает свойство класса (только для чтения) и присваивает ему значение.
   var значение будет изменяемым


2. Интерфейсы зачем нужны? И где используются?

   Это интерфейсы набор функциональности, который должен реализовать класс
   Интерфейс объявляет, "что" должен уметь делать класс (какие методы и свойства у него должны быть)
   Мы можем менять реализацию интерфейса, не меняя код, который их использует.

***Ответы на вопросы по калькулятору***

1. Как реализовать при нажатии рандомный цвет кнопки?

   Используя:
    - postDelayed - для задержки перед выполнением выполнения
      ```kotlin
      button.postDelayed({
             button.setBackgroundColor(Color.parseColor("#5F6ECA"))
         },250)
      ```
    - Random - для генерации случайных цветов
      ```kotlin
      val red = Random.nextInt(256)
      val green = Random.nextInt(256) 
      val blue = Random.nextInt(256)
      ```
    - Color.rgb - для создания цвета из компонентов
      ```kotlin
      val color = Color.rgb(red, green, blue)
      ```
    - setBackgroundColor - для установки цвета и возврата на исходный цвет
      ```kotlin
      button.setBackgroundColor(color) 
      ```

---

   ***Описание:***

   - Работа с сервером C++ 
   - Работа с 2-мя потоками: run_server, run_gui
   - Работа с frontend сервера (imgui,implot) 
   - Работа с zmq

   Программа Связывает сокеты и передает свое месторасположение на сервер
   в сыром виде в формате json. 
   
   Сервер обрабатывает и выводит пользователю результат одновременно записывая в json на сервере







