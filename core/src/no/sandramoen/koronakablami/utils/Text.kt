package no.sandramoen.koronakablami.utils

import com.badlogic.gdx.files.FileHandle

/*
* "Load a simple text file through asset manager in libgdx"
*
* Copied from @author: RegisteredUser
* https://gamedev.stackexchange.com/questions/101326/load-a-simple-text-file-through-asset-manager-in-libgdx
* */
class Text {
    private var string: String

    constructor() {
        string = String("".toByteArray())
    }

    constructor(data: ByteArray?) {
        string = String(data!!)
    }

    constructor(string: String) {
        this.string = String(string.toByteArray())
    }

    constructor(file: FileHandle) {
        string = String(file.readBytes())
    }

    constructor(text: Text) {
        string = String(text.getString().toByteArray())
    }

    fun setString(string: String) {
        this.string = string
    }

    fun getString(): String {
        return string
    }

    fun clear() {
        string = String("".toByteArray())
    }
}
