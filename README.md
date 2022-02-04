# 使用方法

- 继承了实现inspect接口的类`BaseInspectBean`，我们会直接执行inspect方法
  - 检查是否在方法标注了
- 没有继承但是标注了@InspectBean注解或@RequestBody注解(我们不相信你不需要校验它
  - 这种适合简单的，不需要情况校验的类
  - 我们会直接对类进行校验