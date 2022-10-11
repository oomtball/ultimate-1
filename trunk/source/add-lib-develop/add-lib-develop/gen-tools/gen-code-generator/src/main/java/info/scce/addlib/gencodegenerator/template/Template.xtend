package info.scce.addlib.gencodegenerator.template

abstract class Template<T> {

    abstract def CharSequence instantiate(T ast)
}
