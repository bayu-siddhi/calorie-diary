package com.example.calorie_diary

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.example.calorie_diary.data.model.Food
import com.example.calorie_diary.data.source.FoodSource

class SearchFoodActivity : AppCompatActivity() {
    private lateinit var typeFoodInput: EditText
    private lateinit var searchButton: Button
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var foods: List<Food>
    private lateinit var allFoods: List<Food>
    private lateinit var displayedFoods: MutableList<Food>
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search_food)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi RecyclerView dan Adapter
        foodRecyclerView = findViewById(R.id.foodRecyclerView)
        allFoods = FoodSource().getFoodArrayList()
        foodAdapter = FoodAdapter(this, allFoods)
        foodRecyclerView.layoutManager = LinearLayoutManager(this)
        foodRecyclerView.adapter = foodAdapter

        // Inisialisasi EditText dan Button
        typeFoodInput = findViewById(R.id.typeFoodInput)
        searchButton = findViewById(R.id.searchButton)
        backButton = findViewById(R.id.backButton)

        // Set OnClickListener untuk searchButton
        searchButton.setOnClickListener {
            val searchText = typeFoodInput.text.toString().trim()
            foodAdapter.filter(searchText)
        }

        // Set OnClickListener untuk backButton
        backButton.setOnClickListener {
            val intent = Intent(this@SearchFoodActivity, MainActivity::class.java)
            startActivity(intent)
        }

        val sharedPreferences = getSharedPreferences("calorie_data", MODE_PRIVATE)
        val totalCalories = sharedPreferences.getFloat("totalCalories", 0f)
        val maxCalories = sharedPreferences.getFloat("maxCalories", 0f)
        val textView2 = findViewById<TextView>(R.id.textView2)
        textView2.text = "${totalCalories.toInt()}/${maxCalories.toInt()} cals"
    }

}

data class Food(val name: String, val description: String, val calories: Double)

class FoodAdapter(private val context: SearchFoodActivity, private var foodList: List<Food>) :
    RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    private var filteredFoodList: List<Food> = foodList.toMutableList()

    class FoodViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val foodName: TextView = itemView.findViewById(R.id.food)
        val foodDesc: TextView = itemView.findViewById(R.id.food_desc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview, parent, false)
        return FoodViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val currentItem = filteredFoodList[position]
        holder.foodName.text = currentItem.name
        val caloriesPer100g = currentItem.calories * 100
        holder.foodDesc.text = "%.2f Cal, (100 g)".format(caloriesPer100g)

        holder.itemView.setOnClickListener { // Add click listener to itemView
            val intent = Intent(context, AddFoodActivity::class.java) // Create Intent
            intent.putExtra("foodName", currentItem.name) // Pass food name to AddFoodActivity
            context.startActivity(intent) // Start AddFoodActivity
        }
    }

    override fun getItemCount(): Int {
        return filteredFoodList.size
    }

    fun filter(text: String) {
        filteredFoodList = if (text.isEmpty()) {
            foodList
        } else {
            foodList.filter { it.name.toLowerCase().contains(text.toLowerCase()) }
        }
        notifyDataSetChanged()
    }
}