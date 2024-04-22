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

```vue
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

```vue
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

```vue
const store=userStore();
store.state.user?.loginUser

store.dispatch("user/getLoginUser",{
userName:"fishman",
});
```

