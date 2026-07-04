cmake_minimum_required(VERSION 3.22.1)
project(streamnative)

# 编译真正的 streamnative Markdown/XML 流式解析器。
# 必须与 Java/Kotlin 中 System.loadLibrary("streamnative") 保持一致，
# 生成 libstreamnative.so，否则运行时会报 UnsatisfiedLinkError。
set(STREAMNATIVE_SRC
    streamnative/StreamOperators.cpp
    streamnative/native_markdown_splitter.cpp
    streamnative/native_xml_splitter.cpp
    streamnative/plugins/StreamMarkdownPlugin.cpp
    streamnative/plugins/StreamXmlPlugin.cpp
)

add_library(streamnative SHARED ${STREAMNATIVE_SRC})
target_include_directories(streamnative PRIVATE ${CMAKE_SOURCE_DIR})
target_link_libraries(streamnative android log)

# sherpa-onnx-jni 仍为 stub，当前未使用。
add_library(sherpa-onnx-jni SHARED empty.cpp)
target_link_libraries(sherpa-onnx-jni android log)
