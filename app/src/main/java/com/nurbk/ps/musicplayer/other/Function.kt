package com.nurbk.ps.musicplayer.other

import android.content.ContentUris
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.GradientDrawable
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.nurbk.ps.musicplayer.R
import java.util.*

fun permission(
    context: Context,
    permission: ArrayList<String>,
    onComplete: () -> Unit,
    inNotComplete: () -> Unit,
) {
    Dexter.withContext(context)
        .withPermissions(
            permission
        )
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                report?.let {
                    if (report.areAllPermissionsGranted()) {
                        onComplete()
                    } else {
                        inNotComplete()
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: MutableList<PermissionRequest>?,
                token: PermissionToken?,
            ) {

                token?.continuePermissionRequest()
            }
        })
        .withErrorListener {

        }
        .check()
}


fun getImage(
    context: Context,
    idAlbum: Long,
    imageView: ImageView,
    placeholder: Int = R.drawable.xd,
) {
    Glide.with(context)
        .load(
            ContentUris.withAppendedId(
                Uri.parse("content://media/external/audio/albumart"),
                idAlbum
            ).toString()
        )
        .apply(
            RequestOptions()
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
        )
        .thumbnail(0.1f)
        .transition(
            DrawableTransitionOptions()
                .crossFade()
        )
        .into(imageView)
}


fun metaData(
    context: Context,
    uri: Long,
    image: ImageView,
//    imageBg: ImageView,
    layout: View,
    title: TextView,
    subTitle: TextView,
    timeStart: TextView,
    timeEnd: TextView,
) {
    getImage(context, uri, image)
    try {
        val d = ContentUris.withAppendedId(
            Uri.parse("content://media/external/audio/albumart"),
            uri
        )
        val bitmap =
            MediaStore.Images.Media.getBitmap(context.contentResolver, d)
        Palette.from(bitmap).generate { palette ->
            val switcher = palette!!.dominantSwatch
            if (switcher != null) {
                val gradientDrawable = GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    intArrayOf(switcher.rgb, 0x00000000)
                )
//                    imageBg.background = gradientDrawable

                val gradientDrawableBg = GradientDrawable(
                    GradientDrawable.Orientation.BOTTOM_TOP,
                    intArrayOf(switcher.rgb, switcher.rgb)
                )
                layout.background = gradientDrawableBg
                title.setTextColor(switcher.titleTextColor)
                subTitle.setTextColor(switcher.bodyTextColor)
                timeStart.setTextColor(switcher.bodyTextColor)
                timeEnd.setTextColor(switcher.bodyTextColor)
            }
        }
    } catch (e: Exception) {

    }
}


fun getAlbumArt(uri: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(uri)
    return retriever.embeddedPicture
}
