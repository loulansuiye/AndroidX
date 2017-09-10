package com.pingfangx.translator

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.inputmethod.EditorInfo
import android.widget.RadioButton
import com.pingfangx.translator.base.BaseActivity
import com.pingfangx.translator.base.IntentUtils
import kotlinx.android.synthetic.main.activity_translator.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.FileReader
import java.io.FileWriter

class TranslatorActivity : BaseActivity() {
    val REQUEST_PERMISSION = 1
    val mTranslation: Array<String> by lazy {
        getTranslation()
    }
    var mIndex: Int = 0

    var mCurrentKey: String = ""
    var mCurrentValueArray: List<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_translator)
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            arrayOf<String>(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (EasyPermissions.hasPermissions(this, *perms)) {
            initViews()
        } else {
            EasyPermissions.requestPermissions(this, "请允许读取文件权限", REQUEST_PERMISSION, *perms)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
        super.onPermissionsGranted(requestCode, perms)
        initViews()
    }

    fun initViews() {
        val path = IntentUtils.getExtra(intent)
        path?.log()
        var index = 0
        for (i in 0 until mTranslation.size) {
            index = i
            val line: String = mTranslation[i]
            val keyAndValue = line.split("=")
            val value: String = keyAndValue[1]
            val valueArray: List<String> = value.split('|')
            if (valueArray.size > 1) {
                break
            }
        }
        showTranslation(index)
        btn_pre.setOnClickListener({ showTranslation(mIndex - 1) })
        btn_next.setOnClickListener({ showTranslation(mIndex + 1) })
        btn_save.setOnClickListener { saveTranslation(et_translation.text.toString()) }
        btn_save_empty.setOnClickListener { saveTranslation("") }
        et_translation.setSingleLine(true)
        et_translation.inputType = EditorInfo.TYPE_CLASS_TEXT
        et_translation.setOnEditorActionListener({ _, i, _ ->
            if (i == EditorInfo.IME_ACTION_GO) {
                saveTranslation(et_translation.text.toString())
                true
            } else {
                false
            }
        })
    }

    fun showTranslation(index: Int) {
        if (index < 0 || index > mTranslation.size) {
            return
        }
        mIndex = index
        tv_index.setText("${mIndex + 1}/${mTranslation.size}")
        val line: String = mTranslation[mIndex]
        val keyAndValue = line.split("=")
        if (keyAndValue.size < 2) {
            toast("line at $mIndex wrong")
        }
        mCurrentKey = keyAndValue[0]
        tv_english.setText(mCurrentKey)
        val value: String = keyAndValue[1]
        mCurrentValueArray = value.split('|')
        et_translation.setText(mCurrentValueArray[0])
        radio_group.removeAllViews()

        for (i in 0 until mCurrentValueArray.size) {
            val radioButton = RadioButton(this)
            radioButton.id = i
            radioButton.text = mCurrentValueArray[i]
            radioButton.height = 200
            radio_group.addView(radioButton)
        }
        radio_group.setOnCheckedChangeListener { _, i -> saveTranslation(mCurrentValueArray[i]) }
    }

    fun getTranslation(): Array<String> {
        val fileName = Environment.getExternalStorageDirectory().path + "/xx/dict_diff.txt"
        val fileReader = FileReader(fileName)
        val lindes = fileReader.readLines()
        fileReader.close()
        return lindes.toTypedArray()
    }

    fun saveTranslation(translation: String) {
        mTranslation[mIndex] = mCurrentKey + "=" + translation
        saveTranslationToFile()
        showTranslation(mIndex + 1)
    }

    fun saveTranslationToFile() {
        val fileName = Environment.getExternalStorageDirectory().path + "/xx/dict_diff.txt"
        val fileWriter = FileWriter(fileName)
        fileWriter.write(mTranslation.joinToString("\n"))
        fileWriter.close()
        "write success".log()
    }
}
