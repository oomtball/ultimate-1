FROM registry.gitlab.com/scce/add-lib/crossbuild-git:8c126bd

# Download dependencies
RUN git clone --depth 1 --branch cudd-3.0.0 https://github.com/ivmai/cudd
RUN wget https://github.com/trolando/sylvan/archive/refs/tags/v1.6.1.tar.gz && tar xzf v1.6.1.tar.gz
RUN apt-get -qq install openjdk-8-jdk libgmp-dev && apt-get -qq clean


# platform-specific configs
ENV CROSS_TRIPLE=x86_64-w64-mingw32

# Install CUDD
WORKDIR /workdir/cudd
RUN crossbuild autoreconf && \
    crossbuild ./configure --includedir=/usr/x86_64-w64-mingw32/include --host=x86_64-w64-mingw32 --with-pic && \
    crossbuild make && \
    crossbuild make install

# # Return to initial workdir
WORKDIR /workdir