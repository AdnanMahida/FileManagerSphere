package com.etb.filemanager.manager.files.filelist

import com.etb.filemanager.files.provider.archive.common.mime.MimeType

class PickOptions(
    val readOnly: Boolean,
    val pickDirectory: Boolean,
    val mimeTypes: List<MimeType>,
    val localOnly: Boolean,
    val allowMultiple: Boolean
)