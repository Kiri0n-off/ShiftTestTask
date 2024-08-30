<h1>ShiftTestTask</h1> 
Версия java 22.0.2
<h2>Компиляция</h2>
1. Компиляция из исходного кода в байт-код <br>
<code>javac org/example/Controller.java</code> <br>
2. Создание архива с классами <br>
<code>jar -cfm util.jar org/example/MANIFEST.txt org/example/Controller.class</code> <br>
3. Запуск утилиты <br>
<code>java -jar util.jar [OPTION...] [PATH TO FILES TO READ]</code> <br>
