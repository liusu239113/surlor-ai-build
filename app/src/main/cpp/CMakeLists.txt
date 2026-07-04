cmake_minimum_required(VERSION 3.22.1)
project(streamnative_stub)

# 简化构建：跳过所有第三方原生库依赖
# 此 stub 仅编译一个空的 .so 文件，确保 Gradle 构建通过
# 完整的原生功能需要初始化 git 子模块后重新编译

add_library(stream_native SHARED empty.cpp)
target_link_libraries(stream_native android log)

add_library(sherpa-onnx-jni SHARED empty.cpp)
target_link_libraries(sherpa-onnx-jni android log)
