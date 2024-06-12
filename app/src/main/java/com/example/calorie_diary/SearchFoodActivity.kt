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

class SearchFoodActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var typeFoodInput: EditText
    private lateinit var searchButton: Button
    private lateinit var foodRecyclerView: RecyclerView
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var foods: List<Food>
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
        foods = getFoodData()
        foodAdapter = FoodAdapter(foods)
        foodRecyclerView.layoutManager = LinearLayoutManager(this)
        foodRecyclerView.adapter = foodAdapter

        // Inisialisasi EditText dan Button
        typeFoodInput = findViewById(R.id.typeFoodInput)
        searchButton = findViewById(R.id.searchButton)

        // Set OnClickListener untuk searchButton
        searchButton.setOnClickListener {
            val searchText = typeFoodInput.text.toString().trim()
            foodAdapter.filter(searchText)
        }

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val intent = Intent(this@SearchFoodActivity, MainActivity::class.java)
        startActivity(intent)
    }

    // Fungsi untuk mendapatkan data makanan
    private fun getFoodData(): List<Food> {
        val foods = ArrayList<Food>()
        foods.add(Food("Banana", "93 Cal, 1 banana (100 g)"))
        foods.add(Food("Apple", "95 Cal, 1 apple (100 g)"))
        foods.add(Food("Orange", "62 Cal, 1 orange (100 g)"))
        foods.add(Food("Grapes", "69 Cal, 1 cup (151 g)"))
        foods.add(Food("Strawberries", "32 Cal, 1 cup (144 g)"))
        foods.add(Food("Watermelon", "30 Cal, 1 cup (152 g)"))
        foods.add(Food("Avocado", "160 Cal, 1 avocado (201 g)"))
        foods.add(Food("Pineapple", "83 Cal, 1 cup (165 g)"))
        foods.add(Food("Pear", "102 Cal, 1 pear (178 g)"))
        foods.add(Food("Blueberries", "84 Cal, 1 cup (148 g)"))
        foods.add(Food("Peach", "59 Cal, 1 peach (150 g)"))
        foods.add(Food("Mango", "99 Cal, 1 mango (336 g)"))
        foods.add(Food("Cherries", "87 Cal, 1 cup (154 g)"))
        foods.add(Food("Kiwi", "42 Cal, 1 kiwi (69 g)"))
        foods.add(Food("Cantaloupe", "53 Cal, 1 cup (177 g)"))
        foods.add(Food("Pomegranate", "83 Cal, 1 pomegranate (282 g)"))
        foods.add(Food("Apricot", "48 Cal, 1 apricot (35 g)"))
        foods.add(Food("Plum", "30 Cal, 1 plum (66 g)"))
        foods.add(Food("Raspberry", "64 Cal, 1 cup (123 g)"))
        foods.add(Food("Blackberries", "62 Cal, 1 cup (144 g)"))
        return foods
    }

}

data class Food(val name: String, val description: String)

class FoodAdapter(private var foodList: List<Food>) :
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
        holder.foodDesc.text = currentItem.description
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
