#　OJ判题系统构建文档

### 初衷：

为什么要写一个OJ判题系统？

1. 首先是为了填补自己的技术空缺（RocketMQ、代码沙箱、JVM虚拟机、Spring Cloud 微服务）
2. 其次是一般没有多少人想写这样的项目

## 项目分析

做任何系统或者平台都应该先去调研参考一下，能拿到源码就最好，至少省去一些重复性工作，另外去参考别人的设计模式和顶层设计，看看数据库字段

核心业务：

![image-20240422210039892](C:\Users\fishman\AppData\Roaming\Typora\typora-user-images\image-20240422210039892.png)

大致基础业务：

![image-20240422210122145](C:\Users\fishman\AppData\Roaming\Typora\typora-user-images\image-20240422210122145.png)

## 需求分析

1. 题目模块
   1. 创建题目（admin）
   2. 删除、修改题目（admin）
   3. 搜索题目（user）
   4. 在线做题
   5. 对题目点赞收藏
   6. 提交题目
   7. 异步得到题目结果
2. 用户模块
   1. 注册
   2. 登录
   3. 用户信息修改
   4. （充值）
   5. （题目讨论）
3. 判题模块（核心）
   1. 提交以后到哪里？
   2. 除了对错结果输出还有哪些错误处理（内存溢出、超时）
   3. 自主实现（代码沙箱）还是接口调用？
   4. 将功能整合成一个开放的接口

TODO：要不要做一个多语言版本？

## 技术选型

前端：

Vue3框架、Arco Design组件、在线代码编辑器（富文本编辑器？）

后端：

涉及到Java进程、Java安全管理器、JVM、虚拟机、Docker（代码沙箱）、Spring Cloud微服务、消息队列、设计模式

## 架构设计

![image-20240422210522400](C:\Users\fishman\AppData\Roaming\Typora\typora-user-images\image-20240422210522400.png)





# 前端初始化

安装nodejs（去官网下载安装以后会自动包含npm）

安装目录记得改

![image-20240422234608785](C:\Users\fishman\AppData\Roaming\Typora\typora-user-images\image-20240422234608785.png)

global就是全局安装的目录（去.noderc里面改），里面是modules，还要记得去用户环境变量和系统环境变量里面增加

![image-20240422234710249](C:\Users\fishman\AppData\Roaming\Typora\typora-user-images\image-20240422234710249.png)

使用vue-cli脚手架 http://cli.vuejs.org/zh/，脚手架 自动整合了vue-router全局路由管理

还可以去IDEAweb里面修改代码美化

![image-20240422235017468](C:\Users\fishman\AppData\Roaming\Typora\typora-user-images\image-20240422235017468.png)

接着再去引入一个组件库 arco.desgin http://arco.design/vue

## 构建项目通用布局

新建一个layouts文件夹，创建一个BasicLayout.vue布局组件，去组件库里面引入一个layout布局，在引入菜单组件

步骤：

1. 提取通用路由文件
2. 菜单组件读取路由，动态渲染菜单项
3. 绑定跳转事件
4. 同步路由的更新到菜单项高亮

同步高亮原理：首先点击菜单项=>触发点击事件，跳转更新路由=>更新路由后，同步去更新菜单栏的高亮状
态。

使用Vue Router的afterEach的钩子实现

```tsx
const router=userRouter();
//默认主页
const selectedKeys=ref(["/"]);
//路由跳转以后更新菜单项
router.afterEach((to,from,failure)=>{
	selectedKeys.value=[to.path];
});
```

## 全局异常管理

vuex: https://uex.vuejs..org/zh/guide/  (vue-cli脚手架已自动引入)

什么事全局状态管理

就是所有页面全局共享的变量

适合作为全局状态的数据：已登录用户信息（每个页面几乎都要用）

Vuex的本质：给你提供了一套增删改查全局变量的APl,只不过可能多了一些功能（比如时间旅行）

![image-20240423000144452](C:\Users\fishman\AppData\Roaming\Typora\typora-user-images\image-20240423000144452.png)

可以直接参考购物车示例：https://github.com/uejs/uex/tree/main/examples/classic/shopping-cart

state:存储的状态信息，比如用户信息
		mutation(尽量同步)：定义了对变量进行增删改（更新）的方法
		actions(支持异步)：执行异步操作，并且触发mutation的更改(actions调用		mutation)
		modules(模块)：把一个大的state(全局变量)划分为多个小模块，比如user专门存用户的状态信息

在store文件夹里面定义user模板

```tsx
//initial state
import { StoreOptions }from "vuex"
export default {
	namespace:true,
	state:()=>({
	loginUser:{
	userName:"未登录",
	},
	}),
	actions:{
		getLoginUser({commit,state},payload){
			commit("updateUser",{userName:"fishman"});
		},
	},
	mutations:{
		updateUser(state,payload){
			state.loginUser=payload;
		},
	},
}as StoreOptions<any>;
```

接着在index.ts里面导入user模板

```tsx
import {createStore} from "vuex";
import user from "./user";
export default createStore({
	mutation:{},
	actions:{},
	modules:{
		user;
	},
});
```

再去vue里面引入储存变量

```tsx
const store=userStore();
store.state.user?.loginUser

store.dispatch("user/getLoginUser",{
userName:"fishman",
});
```



### 全局权限管理
​		目标：能够直接以一套通用的机制，去定义哪个页面需要那些权限。而不用每个页面独立去判断权限，提高效率。
思路：

1. 在路由配置文件，定义某个路由的访问权限
   2.在全局页面组件app.vue中，绑定一个全局路由监听。每次访问页面时，根据用户要访问页面的路由信息，
   先判断用户是否有对应的访问权限。
   3,如果有，跳转到原页面；如果没有，拦截或跳转到401鉴权或登录页

```tsx
const router useRouter();
const store usestore();
router.beforeEach((to,from,next)=>
//仅管理员可见，判断当前用户是否有权限
if (to.meta?.access =="canAdmin"){
if (store.state.user.loginuser?.role !="admin"){
next("/noAuth");
return;
}
}
next();
});
```

接着优化底部、comtent和globalHeader

### 控制菜单显隐

1. 在routes俩名给冷酷有新增加一个标志位

```tsx
{
path:"/hide",
name:"隐藏页面"，
component HomeView,
meta:
hideInMenu:true,
},
},
```

不要用v-for或者v-if去循环渲染元素，只需要过滤展示元素的数组就行

```tsx
//展示在菜单的路由数组
const visibleRoutes routes.filter((item,index)=>{
if (item.meta?.hideInMenu){
return false;
}
return true;
});
```

### 根据权限隐藏菜单

新建一个access目录，定义一个专门的文件来控制权限

```tsx
const ACCESS_ENUM={
	NOT_login:"notlogin",
	USER:"user",
	ADMIN:"admin",
};
export default ACCESS_ENUM;
```

再定义一个公共权限获取方法，创建checkAccess

```tsx
import ACCESS ENUM from "@/access/accessEnum";
/*检查权限（判断当前登录用户是否具有某个权限）
@param loginuser当前登录用户
@param needAccess需要有的权限
@return boolean有无权限
*/
const checkAccess (loginUser:any,needAccess ACCESS_ENUM.NOT LOGIN)=>{
/获取当前登录用户具有的权限（如果没有loginUser,则表示未登录）
const loginUserAccess loginUser?.userRole ?ACCESS ENUM.NOT LOGIN;
if (needAccess ==ACCESS ENUM.NOT LOGIN){
return true;
}
//如果用户登录才能访问
if (needAccess ==ACCESS_ENUM.USER){
//如果用户没登录，那么表示无权限
if (loginUserAccess ==ACCESS_ENUM.NOT_LOGIN){
return false;
}
}
//如果需要管理员权限
if (needAccess ==ACCESS ENUM.ADMIN)
//如果不为管理员，表示无权限
if (loginuserAccess !=ACCESS_ENUM.ADMIN){
return false;
return true;
};
export default checkAccess;
```

修改GlobalHeader菜单根据权限不同重新渲染

```tsx
const visibleRoutes computed(()=
return routes.filter((item,index)=>{
if (item.meta?.hideInMenu){
return false;
}
/根据权限过滤菜单
if(
checkAccess(store.state.user.loginuser,item?.meta?.access as string)
){
return false;
}
return true;
});
});
```

### 全局项目入口

app.vue里面增加全局初始化函数

```ts
//全局初始化函数，有全局单次调用的代码，都可以写到这里
const doInit ()=
console.log("hello欢迎来到我的项目")；
}
onMounted(()=>
doInit();
})；
```

# 后端初始化

模板解读：

1. sql/create table.sql定义了数据库的初始化建库建表语句sql/post_es_mapping,json帖子表在ES中的建表语句
2. aop:用于全局权限校验、全局日志记录
3. common:万用的类，比如通用响应类
4. config:用于接收application.yml中的参数，初始化一些客户端的配置类（比如对象存储客户端）
5. constant:定义常量
6. controller:接受请求
7. esdao:类似mybatis的mapper,用于操作ES
8. exception:异常处理相关
9. job:任务相关（定时任务、单次任务）
10. manager:服务层（一般是定义一些公用的服务、对接第三方API等
11. mapper:mybatis的数据访问层，用于操作数据库
12. mode:数据模型、实体类、包装类、枚举值
13. service:服务层，用于编写业务逻辑
14. utils:工具类，各种各样公用的方法
15. wxmp:公众号相关的包
16. test:单元测试
17. MainApplication:项目启动入口

# 前后端联调

使用请求工具类Axios：https://axios-http.com/docs/into

使用npm安装以后再引入一个类似于openAPI的后端请求一键生成工具

https://github.com/ferdikoomen/openapi-typescript-codegen

根据文档执行一键生成命令

```sh
openapi --input http://localhost:8121/api/v2/api-docs --output ./generated --client axios
```

还可以使用一键生成大代码进行全局参数修改

```ts
export const OpenAPI:OpenAPIConfig ={
BASE:'http://localhost:3000/api',
VERSION:2.0',
WITH CREDENTIALS:false,
CREDENTIALS:'include,
TOKEN:undefined,
USERNAME:undefined,
PASSWORD:undefined,
HEADERS:undefined,
ENCODE PATH:undefined,
};
```

接着定义axios全局拦截器

```ts
//Add a request interceptor
import axios from "axios";
axios.interceptors.request.use(
function (config){
//Do something before request is sent
return config;
},
function (error){
//Do something with request error
return Promise.reject(error);
)
//Add a response interceptor
axios.interceptors.response.use(
function (response){
console.log("响应"，response);
//Any status code that lie within the range of 2xx cause this function to trigger
//Do something with response data
return response;
},
function (error){
//Any status codes that falls outside the range of 2xx cause this function to trigger
//Do something with response error
return Promise.reject(error);
);
```

### 用户登录实现

自动登录，在store\user里面添加

```ts
actions:{
async getLoginuser({commit,state }payload){
//从远程请求获取登录信息
const res await UserControllerService.getLoginuserusingGet();
if (res.code ===0){
commit("updateuser",res.data);
else
commit("updateuser",
..state.loginuser,
userRole:ACCESS ENUM.NOT LOGIN,
})
},
},
```

在哪里去触发getLoginUser的函数执行？应该在矮一个全局的位置

1. 路由拦截 √ 
2. 全局路由入口app
3. 全局通用布局

然后对全局权限进行优化

新建一个access\index，把原来的路由拦截和权限校验逻辑放在里面

这样的话只要不引入对页面权限就不会开启，适合一些不需要用户权限的页面

自动登录

```ts
const loginuser store.state.user.loginser;
//如果之前没登陆过，自动登录
if (!loginuser !loginuser.userRole){
//加await是为了等用户登录成功之后，再执行后续的代码
await store.dispatch("user/getLoginuser");
```

完整代码：

```ts
import router from "@/router";
import store from "@/store";
import ACCESS ENUM from "@/access/accessEnum";
import checkAccess from "@/access/checkAccess";
router.beforeEach(async (to,from,next)=>{
console.log("登陆用户信息"，store.state.user.loginuser);
const loginUser store.state.user.loginUser;
//如果之前没登陆过，自动登录
if (!loginuser !loginuser.userRole){
//加await是为了等用户登录成功之后，再执行后续的代码
await store.dispatch("user/getLoginuser");
const needAccess (to.meta?.access as string)??ACCESS ENUM.NOT LOGIN;
//要跳转的页面必须要登陆
if (needAccess !=ACCESS ENUM.NOT LOGIN)
//如果没登陆，跳转到登录页面
if (!loginUser !loginUser.userRole){
next('/user/login?redirect=${to.fullPath}');
return;
}
//如果已经登陆了，但是权限不足，那么跳转到无权限页面
if (checkAccess(loginuser,needAccess)){
next("/noAuth");
return;
}}
next();
});
```

### 支持多套布局

在route路由里面新建路由使用vue-route自带的子路由机制进行布局嵌套实现

```ts
export const routes:Array<RouteRecordRaw>=[
{
path:"/user",
name:"用户"，
component:UserLayout,
children:
{
path:"/user/login",
name:"用户登录"，
component:UserLoginView,
},
{
path:"/user/register",
name:"用户注册"，
component:UserRegisterview,
},
],
},
]
```

然后在app全局入口里面引入多套路由

```vue
<template v-if="route.path.startswith('/user')">
<router-view /
</template>
<template v-else>
<BasicLayout /
</template>
```

登录页面开发就看组件库文档

# 后端接口开发

## 库表设计

详情请参考项目源码里面sql目录下的文件

题目标签可以使用一个json对象，爬梯配置和判题用例都可以使用json对象

使用json对象什么好处：便于修改，因为一开始不确定数据项具体有什么，添加到时候改库是重量级操作，一般可以在json里面添加字段

### 注意

如果判题用例太大（超过512kb）就要考虑数据库性能，可以使用本地文件，数据库里面储存测试用例的具体地址

建表以后还要构建一些枚举值

判题信息枚举值：
Accepted成功
·Wrong Answer答案错误
·Compile Error编译错误
·Memory Limit Exceeded内存溢出
·Time Limit Exceeded超时
·Presentation Error展示错误
·Output Limit Exceeded输出溢出
·Waiting等待中
·Dangerous Operation危险操作
·Runtime Error运行错误（用户程序的问题）
·System Error系统错误（做系统人的问题）