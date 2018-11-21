package com.cozo.cozomvp.mainactivity.inflatedlayouts.recyclerview

import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import com.cozo.cozomvp.R
import com.cozo.cozomvp.usercart.*
import kotlinx.android.synthetic.main.cardview_address.view.*
import kotlinx.android.synthetic.main.cardview_order.view.*
import kotlinx.android.synthetic.main.cardview_preview_delivery.view.*

class CartItemsRecyclerViewAdapter(private val listener: RecyclerListener) : RecyclerView.Adapter<CartItemsRecyclerViewAdapter.RecyclerViewHolder>() {

    private var itemList: List<OrderModel> = listOf()
    private lateinit var address: String
    private var priceRange: PriceRange? = null
    private var timeRange: TimeRange? = null

    interface RecyclerListener{
        fun onRemoveItemClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view: View = when(viewType){
            VIEW_TYPE_ADDRESS -> LayoutInflater.from(parent.context).inflate(R.layout.cardview_address, parent, false)
            VIEW_TYPE_PREVIEW_DELIVERY -> {
                if (priceRange == null && timeRange == null){
                    LayoutInflater.from(parent.context).inflate(R.layout.cardview_loading_preview_delivery, parent, false)
                } else {
                    LayoutInflater.from(parent.context).inflate(R.layout.cardview_preview_delivery, parent, false)
                }
            }
            VIEW_TYPE_ORDERS_LABEL -> LayoutInflater.from(parent.context).inflate(R.layout.cardview_order_label, parent, false)
            VIEW_TYPE_ORDERS -> LayoutInflater.from(parent.context).inflate(R.layout.cardview_order, parent, false)
            else -> LayoutInflater.from(parent.context).inflate(R.layout.cardview_order, parent, false)
        }
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int  = itemList.size + 3

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> VIEW_TYPE_ADDRESS
            1 -> VIEW_TYPE_PREVIEW_DELIVERY
            2 -> VIEW_TYPE_ORDERS_LABEL
            3 -> VIEW_TYPE_ORDERS
            else -> VIEW_TYPE_ORDERS
        }
    }

    fun setOrderList(items: ReviewOrderModel){
        itemList = items.orders
        address = items.formattedAddress
        if (items.priceRange != null && items.timeRange != null) {
            priceRange = items.priceRange
            timeRange = items.timeRange
            notifyItemChanged(1)
        }
    }

    fun updateItemList(updatedList: List<OrderModel>){
        itemList = updatedList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        holder.bindView(position)
    }

    inner class RecyclerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener, PopupMenu.OnMenuItemClickListener, DialogInterface.OnClickListener{

        fun bindView(position: Int){
            when(position){
                0 -> {
                    itemView.addressText.text = address
                    itemView.addressEditIcon.setOnClickListener(this)
                }
                1 -> {
                    if(timeRange != null && priceRange != null) {
                        itemView.previewDeliveryTime.text = itemView.context.getString(R.string.cardview_preview_delivery_time,
                                timeRange?.getFormattedMin(),timeRange?.getFormattedMax())
                        itemView.previewDeliveryPrice.text = itemView.context.getString(R.string.cardview_preview_delivery_price,
                                String.format("%02.2f", priceRange?.min).replace(".",","),
                                String.format("%02.2f", priceRange?.max).replace(".",","))
                    }
                }
                2 -> {}
                else -> {
                    itemView.orderItemName.text = itemList[position-3].item.name
                    itemView.orderItemQuantity.text = itemView.context.getString(R.string.cardview_order_quantity, itemList[position-3].quantity.toString())
                    itemView.orderItemsPrice.text = itemView.context.getString(R.string.cardview_order_items_price, String.format("%02.2f", itemList[position-3].totalPrice).replace(".",","))

                    itemView.orderMoreInfoItemsImage.setOnClickListener(this)
                    itemView.orderItemName.setOnClickListener(this)
                }
            }
        }

        override fun onClick(v: View?) {
            when(v){
                itemView.addressEditIcon -> {
                    showEditTextDialog()
                }
                itemView.orderMoreInfoItemsImage -> {
                    showPopupMenu(v!!)
                }
                itemView.orderItemName -> {
                    if( itemView.orderItemsHiddenComments.visibility == View.GONE){
                        if(itemList[adapterPosition-3].notes != ""){
                            itemView.orderItemsHiddenComments.text = itemView.context.getString(R.string.cardview_order_hidden_comments, itemList[adapterPosition-3].notes)
                            itemView.orderItemsHiddenComments.visibility = View.VISIBLE
                        }
                    } else {
                        itemView.orderItemsHiddenComments.visibility = View.GONE
                    }
                }
            }
        }

        private fun showEditTextDialog(){
            val alert = AlertDialog.Builder(itemView.context)
            alert.setTitle("Seu endereço de entrega")
            alert.setMessage("Por favor, preencha com seu endereço completo")
            val input = EditText(itemView.context)
            input.hint = itemView.addressText.text
            alert.setView(input)
            alert.setPositiveButton("Ok", this)
            alert.show()
        }

        override fun onClick(dialog: DialogInterface?, which: Int) {
            //do something with updated address
        }

        private fun showPopupMenu(view: View){
            val popup = PopupMenu(itemView.context, view)
            val inflater = popup.menuInflater
            inflater.inflate(R.menu.cardview_order_menu, popup.menu)
            popup.setOnMenuItemClickListener(this)
            popup.show()
        }

        override fun onMenuItemClick(item: MenuItem?): Boolean {
            when(item!!.itemId){
                R.id.removeItem -> {
                    // notify fragment
                    listener.onRemoveItemClicked(adapterPosition-3)
                    return true
                }
                R.id.editItem -> {
                    return true
                }
                else -> {
                    return false
                }
            }
        }

    }

    companion object {
        const val VIEW_TYPE_ADDRESS = 0
        const val VIEW_TYPE_PREVIEW_DELIVERY = 1
        const val VIEW_TYPE_ORDERS_LABEL = 2
        const val VIEW_TYPE_ORDERS = 3
    }
}