cmake_minimum_required (VERSION 3.5)
project (addlib)

# Compile with C++ 11
set (CMAKE_CXX_STANDARD 11)
set (CMAKE_CXX_FLAGS "-Wall -pedantic -Wextra")

# Sources and include directories
set (INCLUDE_DIRS "include")
file (GLOB_RECURSE SOURCE_FILES "source/*.cpp")

# Statically link gcc libs for windows
# see https://stackoverflow.com/questions/18138635/mingw-exe-requires-a-few-gcc-dlls-regardless-of-the-code?
IF (WIN32)
    set(CMAKE_CXX_STANDARD_LIBRARIES "-static-libgcc -static-libstdc++ ${CMAKE_CXX_STANDARD_LIBRARIES}")
    set(CMAKE_EXE_LINKER_FLAGS "${CMAKE_EXE_LINKER_FLAGS} -Wl,-Bstatic,--whole-archive -lwinpthread -Wl,--no-whole-archive")
ENDIF()

# Include dependencies
find_library (CUDD_LIBRARY "cudd")

# JNI include directories
find_package (JNI)

# Build the ADD-Lib
include_directories (${INCLUDE_DIRS} ${CUDD_INCLUDE_DIRS} ${JNI_INCLUDE_DIRS})
add_library (addlib-cudd SHARED ${SOURCE_FILES})
link_directories (${CUDD_LINK_DIRS})
target_link_libraries (addlib-cudd ${CUDD_LIBRARY})

