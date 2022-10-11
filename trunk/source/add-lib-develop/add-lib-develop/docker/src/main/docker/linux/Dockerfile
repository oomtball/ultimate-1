FROM registry.gitlab.com/scce/add-lib/crossbuild-git:8c126bd

# Download dependencies
RUN git clone --depth 1 --branch cudd-3.0.0 https://github.com/ivmai/cudd
RUN wget https://github.com/trolando/sylvan/archive/refs/tags/v1.6.1.tar.gz && tar xzf v1.6.1.tar.gz
RUN apt-get -qq install openjdk-8-jdk libgmp-dev && apt-get -qq clean


# platform-specific configs
ENV CROSS_TRIPLE=x86_64-linux-gnu

# Install CUDD
WORKDIR /workdir/cudd
RUN autoreconf && \
    ./configure --with-pic && \
    make && \
    make install

# Install sylvan
WORKDIR /workdir/sylvan-1.6.1
RUN cmake \
        -DBUILD_SHARED_LIBS=OFF \
        -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_POSITION_INDEPENDENT_CODE=ON \
        . && \
    make && \
    make install


# Return to initial workdir
WORKDIR /workdir
