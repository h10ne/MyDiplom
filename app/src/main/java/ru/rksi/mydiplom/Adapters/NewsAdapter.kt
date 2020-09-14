package ru.rksi.mydiplom.Adapters

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.makeramen.roundedimageview.RoundedImageView
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import ru.rksi.mydiplom.APIClasses.News
import ru.rksi.mydiplom.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class NewsAdapter() : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    private val News = ArrayList<News>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val date = itemView.findViewById<TextView>(R.id.date)
        private val title = itemView.findViewById<TextView>(R.id.title)
        private val shortText = itemView.findViewById<TextView>(R.id.shortText)
        private val image = itemView.findViewById<RoundedImageView>(R.id.mainImage)

        fun bind(news: News) {
            Picasso.get().load(news.mainImage).placeholder(R.drawable.placeholder_img).into(image)

            if(news.mainImage.isNotEmpty())
            {
                /*
                * Говнокод для обработки клика на картинку и открытия ее на фулскрин
                */
                val list:ArrayList<String> = ArrayList()
                list.add(news.mainImage)
                image.setOnClickListener {
                    StfalconImageViewer.Builder(itemView.context, list) { view, image ->
                        Picasso.get().load(image).into(view)
                    }.show()
                }
            }

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = news.date.toLong() * 1000
            date.text = SimpleDateFormat("dd MMMM yyyy").format(calendar.timeInMillis)

            title.text = news.title
            if (news.shortText.length > 110)
                shortText.text = news.shortText.substring(0, 100) + "..."
            else if(news.shortText.isEmpty())
                shortText.visibility = View.GONE
        }
    }


    fun setItems(news: ArrayList<News>) {
        clearItems()
        News.addAll(news)
        notifyDataSetChanged()
    }

    fun addItems(news: ArrayList<News>) {
        News.addAll(news)
        notifyDataSetChanged()
    }

    fun clearItems() {
        News.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.news_items_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return News.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val news: News = News[position]
        holder.bind(news)

        holder.itemView.setOnClickListener {
            val dialog = LayoutInflater.from(it.context).inflate(R.layout.news_dialog, null)

            val builder = AlertDialog.Builder(it.context).setView(dialog)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = news.date.toLong() * 1000

            dialog.findViewById<TextView>(R.id.date).text = SimpleDateFormat("dd MMMM yyyy").format(calendar.timeInMillis)
            dialog.findViewById<TextView>(R.id.title).text = news.title

            var str: String
            val indexOfHTTPS = news.text.indexOf("https://")
            if (indexOfHTTPS != -1) {
                str = news.text.substring(0, indexOfHTTPS)
                val count = news.text.count() - indexOfHTTPS
                str += " " + news.text.substring(indexOfHTTPS, indexOfHTTPS + count)
            } else
                str = news.text
            dialog.findViewById<TextView>(R.id.fullText).text = str
            Linkify.addLinks(dialog.findViewById<TextView>(R.id.fullText), Linkify.WEB_URLS)
            dialog.findViewById<TextView>(R.id.fullText).movementMethod =
                LinkMovementMethod.getInstance()
            dialog.findViewById<ImageView>(R.id.mainImage).scaleType =
                ImageView.ScaleType.FIT_CENTER

           /*
            * Говнокод для обработки клика на картинку и открытия ее на фулскрин
            */
            val dialogImage = dialog.findViewById<ImageView>(R.id.mainImage)
            Picasso.get().load(news.mainImage).into(dialogImage)
            val list:ArrayList<String> = ArrayList()
            list.add(news.mainImage)
            dialogImage.setOnClickListener {
                StfalconImageViewer.Builder(it.context, list) { view, image ->
                    Picasso.get().load(image).into(view)
                }.show()
            }
            val imageSlider = dialog.findViewById<ImageSlider>(R.id.image_slider)
            val imageList = ArrayList<SlideModel>()

            news.images.forEach {
                if (it != "")
                    imageList.add(SlideModel(it))
            }

            if (imageList.size == 0)
                imageSlider.visibility = View.GONE
            else {
                imageSlider.setImageList(imageList)
                imageSlider.setItemClickListener(object : ItemClickListener {
                    override fun onItemSelected(position: Int) {
                        StfalconImageViewer.Builder(it.context, imageList) { view, image ->
                            Picasso.get().load(image.imageUrl).into(view)
                        }.show()
                    }
                })
            }
            imageSlider.stopSliding()

            val alDialog = builder.create()
            dialog.findViewById<View>(R.id.cross_close).setOnClickListener(fun(_: View) {
                alDialog.dismiss()
            })
            alDialog.setCanceledOnTouchOutside(false)
            alDialog.show()

        }
    }
}