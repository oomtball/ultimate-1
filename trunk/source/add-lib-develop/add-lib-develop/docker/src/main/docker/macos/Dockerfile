FROM registry.gitlab.com/scce/add-lib/crossbuild-git:8c126bd

# Download dependencies
RUN git clone --depth 1 --branch cudd-3.0.0 https://github.com/ivmai/cudd
RUN wget https://github.com/trolando/sylvan/archive/refs/tags/v1.6.1.tar.gz && tar xzf v1.6.1.tar.gz
RUN apt-get -qq install libgmp-dev && apt-get -qq clean


# platform-specific configs
ENV CROSS_TRIPLE=x86_64-apple-darwin

# Install CUDD
WORKDIR /workdir/cudd
RUN crossbuild autoreconf && \
    ./configure CC=/usr/osxcross/bin/o64-clang CXX=/usr/osxcross/bin/o64-clang++ --prefix=/usr/osxcross/SDK/MacOSX10.10.sdk/usr --host=x86_64-apple-darwin --with-pic && \
    crossbuild make && \
    crossbuild make install

# Install sylvan
WORKDIR /workdir/sylvan-1.6.1
RUN ln -s /usr/include/x86_64-linux-gnu/gmp.h /usr/osxcross/SDK/MacOSX10.10.sdk/usr/include/gmp.h
RUN ln -s /usr/lib/x86_64-linux-gnu/libgmp.a /usr/osxcross/SDK/MacOSX10.10.sdk/usr/lib/libgmp.a
RUN crossbuild cmake \
        -DCMAKE_C_FLAGS="-stdlib=libc++ -std=c++11" \
        -DCMAKE_CXX_FLAGS="-stdlib=libc++ -std=c++11" \
        -DCMAKE_INSTALL_PREFIX=/usr/osxcross/SDK/MacOSX10.10.sdk/usr \
        -DBUILD_SHARED_LIBS=OFF \
        -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_POSITION_INDEPENDENT_CODE=ON\
        -DCMAKE_SYSTEM_NAME=Darwin \
        . && \
    crossbuild make && \
    crossbuild make install


# Return to initial workdir
WORKDIR /workdir