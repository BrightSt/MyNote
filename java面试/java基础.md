# java 基础

### 1.Hashtable 与Hashmap 的区别
Hashtable:有synchronized关键字修饰，线程安全，相应的带来效率慢</br>
HashMap:没有synchronized关键字修饰，线程非安全，相应的效率高

### 2.抽象类与接口的区别：
1.is-a 用抽象类</br>
2.has-a 用接口

### 3.final关键字的使用：
#### 分为三种使用场景：</br>
##### 1.修饰在属性上：
属性的值固定，不能修改
##### 2.修饰在方法上
被修饰的方法不能被子类重写
##### 3.修饰在类上
不能被其他类继承


### 4.异常分类和处理机制：
#### 异常分类：
throwable:</br>
error:</br>
exception:</br>
checked 和 runtimeexception

#### 处理机制
抛出异常 捕捉异常

### 5.jdk版本区别

#### jdk1.5
1.自动装箱与拆箱</br>
2.枚举</br>
3.泛型</br>
4.for each</br>

#### jdk1.6
改动不明显

#### jdk 1.7
switch 可以使用字符串

#### jdk 1.8
大的版本更新，而且是较新版本，做单独学习。

#### jdk 1.9
最新jdk版本 单独学习

### 6.StringBuilder内部实现机制
AbstractStringBuilder中采用一个char数组来保存需要append的字符串，char数组有一个初始大小，当append的字符串长度超过当前char数组容量时，则对char数组进行动态扩展，也即重新申请一段更大的内存空间，然后将当前char数组拷贝到新的位置，因为重新分配内存并拷贝的开销比较大，所以每次重新申请内存空间都是采用申请大于当前需要的内存空间的方式，这里是2倍。

### 7.反射机制
参考文章：</br>
http://www.sczyh30.com/posts/Java/java-reflection-1/</br>
需要jvm作为支撑

### 8.test地方
