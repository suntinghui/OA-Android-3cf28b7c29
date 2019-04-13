
#
#一个Android.mk 文件首先必须定义好LOCAL_PATH变量
#它用于在开发树中查找源文件
#在这个例子中，宏函数’my-dir’, 由编译系统提供
#用于返回当前路径（即包含Android.mk file文件的目录）
#
LOCAL_PATH := $(call my-dir)


# ---------------------------------------------------------
# ---------------------------------------------------------
# 通常用到的so文件直接放入libs/armeabi目录中 
# 但对于有jni的工程，so不能直接放入libs/armeabi目录中 
# 可以查看NDK Docs PREBUILTS.html 
# 添加新so
include $(CLEAR_VARS)
#LOCAL_MODULE := jpush205
#LOCAL_SRC_FILES := prebuilt/libjpush205.so
LOCAL_MODULE := jpush219
LOCAL_SRC_FILES := prebuilt/libjpush219.so
include $(PREBUILT_SHARED_LIBRARY)
# ---------------------------------------------------------
# ---------------------------------------------------------

#
#CLEAR_VARS由编译系统提供
#指定让GNU MAKEFILE为你清除许多LOCAL_XXX变量
#（例如 LOCAL_MODULE, LOCAL_SRC_FILES, LOCAL_STATIC_LIBRARIES, 等等...),
#除LOCAL_PATH
#这是必要的，因为所有的编译控制文件都在同一个GNU MAKE执行环境中，所有的变量都是全局的
#
include $(CLEAR_VARS)

# new add
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_LDFLAGS += $(LOCAL_PATH)/lib/libcrypto.a
LOCAL_LDFLAGS += $(LOCAL_PATH)/lib/libssl.a
# LOCAL_LDFLAGS += $(LOCAL_PATH)/test/libhello.a



#
#编译的目标对象，LOCAL_MODULE变量必须定义
#以标识你在Android.mk文件中描述的每个模块。名称必须是唯一的，而且不包含任何空格
#注意：编译系统会自动产生合适的前缀和后缀，换句话说
#一个被命名为'hello-jni'的共享库模块，将会生成'libhello-jni.so'文件
#重要注意事项：
#如果你把库命名为‘libhello-jni’，编译系统将不会添加任何的lib前缀
#也会生成 'libhello-jni.so'
#这是为了支持来源于Android平台的源代码的Android.mk文件，如果你确实需要这么做的话
#
#LOCAL_MODULE    := hello-jni
LOCAL_MODULE    := lkoa
#
#LOCAL_SRC_FILES变量必须包含将要编译打包进模块中的C或C++源代码文件
#注意，你不用在这里列出头文件和包含文件，因为编译系统将会自动为你找出依赖型的文件
#仅仅列出直接传递给编译器的源代码文件就好
#注意，默认的C++源码文件的扩展名是’.cpp’
#指定一个不同的扩展名也是可能的，只要定义LOCAL_DEFAULT_CPP_EXTENSION变量
#不要忘记开始的小圆点（也就是’.cxx’,而不是’cxx’）
#
#LOCAL_SRC_FILES := hello-jni.c
LOCAL_SRC_FILES := signcore/signcore.c \
					sign/sign.c \
					util/util.c

# this is debug logcat
LOCAL_LDLIBS += -llog

#
#BUILD_SHARED_LIBRARY表示编译生成共享库，是编译系统提供的变量
#指向一个GNU Makefile脚本，负责收集自从上次调用'include $(CLEAR_VARS)'以来
#定义在LOCAL_XXX变量中的所有信息，并且决定编译什么，如何正确地去做
#还有 BUILD_STATIC_LIBRARY变量表示生成静态库：lib$(LOCAL_MODULE).a
#BUILD_EXECUTABLE 表示生成可执行文件。
#
include $(BUILD_SHARED_LIBRARY)


