# Check

​	一个使用简单，代码级别灵活度和极富智能的spring首个第三方校验框架

# 简单使用

> maven

```xml
    <groupId>com.landao</groupId>
    <artifactId>checker-spring-boot-starter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
```

> 结束

​	好了，就是这么简单！

# 功能

## 对象校验

```java
public class TopicInfo{

    @Id
    private Long id;

    /**
     * 标题
     */
    @Check(value = "标题",max= 128)
    private String title;

    /**
     * 内容
     */
    @Check(value = "内容",max = 50000)
    private String content;


}
```

​	如果是简单的，不需要区分任何情况的参数校验，比如用户反馈，这种只有添加没有修改的简单对象。

​	那我在controller需要做什么呢？答案是什么都不需要，只需要你按照原来的接口定义就可以，对原来的代码，没有丝毫的入侵！！！

## 分组校验

```java
public class UserInfo {

    @Id
    private Long id;

    /**
     * 用户名
     * @apiNote 最大长度32
     */
    @Check(value = "用户名",max = 32)
    private String name;

    /**
     * 电话
     */
    @TelePhone
    @UpdateIgnore
    private String telephone;

    /**
     * 头像
     */
    @Nullable
    @Check(value = "头像",max = 128)
    private String avatar;

    /**
     * 个性签名
     */
    @Nullable
    @Check(value = "个性签名",max =64)
    private String signature;

}
```

​	其中@id会在添加的情况下自动设置为null，而且在update的情况，自动判断id是否合法，比如你的id是long类型，结果接受到的结果是一个小于0的数，那肯定是不可能的。

​	怎么指定是什么情况的校验呢？

```java
@RequiredLogin
@UpdateCheck
@PostMapping("/change/info")
public CommonResult<Void> changeInfo(@RequestBody UserInfo userInfo)
```

​	这个简单唉，而且是标注在方法上的，比标注在参数上看起来清爽多了。是呀，本来@RequestBody只标注一个是最合理的做法。

​	那等一下，要是我有多个分组呢？其实一般情况下只有add和update吧。那不行，我的业务就是这么复杂。那也行，@CheckGroup(AddCheck.class)，这样其实就是@AddCheck的原理。

​	那我怎么区分呢，我觉得区分分组的时候实在在太麻烦了，我总不能像spring的那样，一个一个分开定义吧。

## 代码校验

​	这简单，我提供提供代码级别的校验。啊？不会是spring那种需要继承某个类，然后再写校验吧。太麻烦了。

`public class UserInfo implements Checked`

```java
@Override
public void check(Class<?> group,String supperName) {
    if(isAddGroup(group)){
        if(!StringUtils.hasText(password)){
            addIllegal("password","密码不能为空");
        }
        if(password.length()!=32){
            addIllegal("password","密码必须用md5加密");
        }
        password=password.toLowerCase(Locale.ROOT);//转化成小写
    }
    if(!(Objects.equals(sex,"女") || Objects.equals(sex,"男"))){
        addIllegal("sex","性别必须为男或女");
    }
    if(birth==null){
        addIllegal("birth","生日不能为空");
    }else {
        if(birth.isAfter(LocalDate.now())){
            addIllegal("birth","出生日期不能晚于现在");
        }
    }
}
```

​	你只需要重写这个方法就可以了，而且框架会帮助你自动调用，而且你还有一个情况没有考虑到，就是如果我在分组的情况下，检查方法不一样怎么办？其实spring的校验框架是考虑到这种情况的，但其实，我们的参数校验都差不多的。

​	那不错啊，但是你这样自己判断分组，虽然说万能，但是看起来还是比较麻烦的，能不能在方法上标注解就确定它是那种情况呢。可以，但是这样性能不好，我有时间实现吧

## 嵌套校验

​	这个不用多说，类里面还有其他的类，而且会自动生成类抵达路径，可以说看起来不能再方便了。而且你自定义的时候，也可以利用接口的默认实现方法，把supperName传递进来，实现类路径分析！

​	你说的这些我感觉普普通通的，但是你要知道一点，spring扩充的，支持分组校验的框架可没能实现这个功能。那。。确实，但是。。哦，对了，我就想获得一个失败原因怎么呢。

​	这个简单，你直接设置checkMode，在yaml配置一下就可以了。有快速失败的模式，而且我这个快速失败，可不是同try-catch的方法实习的，是一个自定义集合。。停停，我知道性能没有问题，但是我总感觉差点什么。那就是，普通参数你校验吗？

## 参数校验

​	你这问的，问到我最牛逼的功能了！参数校验的话，你可以把用在类上的注解直接用在参数上。哦，但这有什么牛逼的啊？

```java
@PathVariable Long commentId,
@RequestParam(defaultValue = "1") Integer page,
@RequestParam(defaultValue = "15") Integer limit
```

​	看到上面的参数了吗？怎么了，这不是很普通的参数吗，比如id，你看看它是不是小于0，page，limit也不能小于0，然后limit不能太大，不然服务器受不了。。。唉，你怎么那么熟悉啊？因为太常见了呗。但是我和你说，这样就能自动校验了？？？？你说。。。自动校验？

​	对，我会根据参数的名字做检查的，而且你可以使用注解来屏蔽这些检查，就是那种特别特殊的，需要自己弄得。

​	那厉害啊，那岂不是能省去70%的校验了。不不不，很可能是90%。啊，为什么，一个项目怎么能没有一些特殊的呢。对呀，是特殊，但是我让你自定义不就是可以了吗？

```java
@Component
public class AppointmentChecker implements ParamChecker {
    
    @Override
    public boolean supportParameter(String parameterName) {
        return "appointmentDate".equals(parameterName);
    }

    @Override
    public FeedBack handleParameter(Parameter parameter, Object value) {
        LocalDate date=(LocalDate) value;
        if(date.isBefore(LocalDate.now())){
           FeedBack.illegal(parameter.getName(),"预约日期不能早于现在"); 
        }
        return FeedBack.pass();
    }
    
}
```

## 参数反馈

​	上面这个例子不错，但是你这个feedBack是干什么呢？

​	好，我来给你解释，比如字符串，你最讨厌的就是用户加空格什么的，我可以把字符串的空格裁剪调，然后给你设置到对象或者方法的参数里面。牛啊，那xss能预防一下吗？可以啊，只是现在还没有写，就是一个正则的事情。