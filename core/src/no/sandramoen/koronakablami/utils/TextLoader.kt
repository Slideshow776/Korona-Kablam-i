package no.sandramoen.koronakablami.utils

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array

/*
* "Load a simple text file through asset manager in libgdx"
*
* Copied from @author: RegisteredUser
* https://gamedev.stackexchange.com/questions/101326/load-a-simple-text-file-through-asset-manager-in-libgdx
* */
class TextLoader(resolver: FileHandleResolver?) : AsynchronousAssetLoader<Text?, TextLoader.TextParameter?>(resolver) {
    var text: Text? = null

    class TextParameter : AssetLoaderParameters<Text?>()

    override fun loadAsync(manager: AssetManager?, fileName: String?, file: FileHandle?, parameter: TextParameter?) {
        text = null
        text = file?.let { Text(it) }
    }

    override fun loadSync(manager: AssetManager?, fileName: String?, file: FileHandle?, parameter: TextParameter?): Text? {
        val text = text
        this.text = null
        return text!!
    }

    override fun getDependencies(fileName: String?, file: FileHandle?, parameter: TextParameter?): Array<AssetDescriptor<Any>>? {
        return null
    }
}