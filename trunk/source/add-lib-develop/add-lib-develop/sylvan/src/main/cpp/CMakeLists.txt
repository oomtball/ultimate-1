cmake_minimum_required (VERSION 3.5)
project (addlib)

# Compile with C++ 11
set (CMAKE_CXX_STANDARD 11)
#set (CMAKE_CXX_FLAGS "-Wall -pedantic -Wextra")

# Sources and include directories
set (INCLUDE_DIRS "include")
file (GLOB_RECURSE SOURCE_FILES "source/*.cpp")

# Include dependencies
find_library (SYLVAN_LIBRARY "sylvan")

# JNI include directories
find_package (JNI)

# Build the ADD-Lib
include_directories (${INCLUDE_DIRS} ${SYLVAN_INCLUDE_DIRS} ${JNI_INCLUDE_DIRS})
add_library (addlib-sylvan SHARED ${SOURCE_FILES})
link_directories (${SYLVAN_LINK_DIRS})
target_link_libraries (addlib-sylvan ${SYLVAN_LIBRARY})

