package com.example.calorie_diary.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.calorie_diary.data.model.CalorieDiaries
import com.example.calorie_diary.data.model.EatingHistory
import com.example.calorie_diary.data.model.Food
import com.example.calorie_diary.data.model.TodayEatingHistory
import com.example.calorie_diary.data.model.User
import com.example.calorie_diary.util.StringDate

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {
    companion object{
        private const val DATABASE_NAME = "calorie_diary.db"
        private const val DATABASE_VERSION = 1
        private const val USER  = "users"
        private const val FOOD = "foods"
        private const val EATING_HISTORY = "eating_history"
        private const val CALORIE_DIARIES = "calorie_diaries"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val user = ("CREATE TABLE $USER (" +
                " id           INTEGER NOT NULL," +
                " name         TEXT    NOT NULL," +
                " email        TEXT    NOT NULL," +
                " password     TEXT    NOT NULL," +
                " age          INTEGER NOT NULL," +
                " weight       INTEGER NOT NULL," +
                " height       INTEGER NOT NULL," +
                " gender       TEXT    NOT NULL," +
                " is_logged_in INTEGER NOT NULL DEFAULT 0," +
                " PRIMARY KEY (id) );")

        val food = ("CREATE TABLE $FOOD (" +
                " id           INTEGER NOT NULL," +
                " name         TEXT    NOT NULL," +
                " calories     REAL    NOT NULL DEFAULT 0.0," +
                " carbohydrate REAL    NOT NULL DEFAULT 0.0," +
                " proteins     REAL    NOT NULL DEFAULT 0.0," +
                " fat          REAL    NOT NULL DEFAULT 0.0," +
                " PRIMARY KEY (id) );")

        val eatingHistory = ("CREATE TABLE $EATING_HISTORY (" +
                " id          INTEGER NOT NULl," +
                " user_id     INTEGER NOT NULl," +
                " date        TEXT    NOT NULL," +
                " food_id     INTEGER NOT NULL," +
                " food_weight INTEGER NOT NULL," +
                " PRIMARY KEY (id)," +
                " FOREIGN KEY (user_id) REFERENCES $USER (id)," +
                " FOREIGN KEY (food_id) REFERENCES $FOOD (id) );")

        val calorieDiaries = ("CREATE TABLE $CALORIE_DIARIES (" +
                " user_id               INTEGER NOT NULL," +
                " date                  TEXT    NOT NULL," +
                " progress_calories     REAL    NOT NULL DEFAULT 0.0," +
                " max_calories          REAL    NOT NULL DEFAULT 0.0," +
                " progress_carbohydrate REAL    NOT NULL DEFAULT 0.0," +
                " max_carbohydrate      REAL    NOT NULL DEFAULT 0.0," +
                " progress_proteins     REAL    NOT NULL DEFAULT 0.0," +
                " max_proteins          REAL    NOT NULL DEFAULT 0.0," +
                " progress_fat          REAL    NOT NULL DEFAULT 0.0," +
                " max_fat               REAL    NOT NULL DEFAULT 0.0," +
                " PRIMARY KEY (user_id, date)," +
                " FOREIGN KEY (user_id) REFERENCES $USER (id) );")

        db.execSQL(user)
        db.execSQL(food)
        db.execSQL(eatingHistory)
        db.execSQL(calorieDiaries)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        db.execSQL("DROP TABLE IF EXISTS $USER")
        db.execSQL("DROP TABLE IF EXISTS $FOOD")
        db.execSQL("DROP TABLE IF EXISTS $EATING_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $CALORIE_DIARIES")
        onCreate(db)
    }

    fun reCreateTable() {
        val db = this.writableDatabase
        db.execSQL("DROP TABLE IF EXISTS $USER")
        db.execSQL("DROP TABLE IF EXISTS $FOOD")
        db.execSQL("DROP TABLE IF EXISTS $EATING_HISTORY")
        db.execSQL("DROP TABLE IF EXISTS $CALORIE_DIARIES")
        onCreate(db)
    }

    fun deleteAllData() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM  $CALORIE_DIARIES")
        db.execSQL("DELETE FROM  $EATING_HISTORY")
        db.execSQL("DELETE FROM $USER")
        // db.execSQL("DELETE FROM $FOOD")
        db.close()
    }

    /* USER ======================================================================================*/
    fun signUp(user: User): Boolean {
        val values = ContentValues()
        val isEmailAvailable = checkUserEmail(user.email)
        if (!isEmailAvailable) {
            if (user.password != null) {
                // TODO: val hash = Bcrypt.hash(user.password, 12)
                values.put("id", user.id)
                values.put("name", user.name)
                values.put("email", user.email)
                values.put("password", user.password)
                values.put("age", user.age)
                values.put("weight", user.weight)
                values.put("height", user.height)
                values.put("gender", user.gender)
                values.put("is_logged_in", user.isLoggedIn)

                val db = this.writableDatabase
                db.insert(USER, null, values)
                db.close()
                return true
            }
        }
        return false
    }

    fun logIn(email: String, password: String): Boolean {
        // TODO: Bcrypt.verify(password, hash_password_in_db)
        val db = this.readableDatabase
        val cursorUser = db.rawQuery("SELECT id, email, password FROM $USER WHERE email = ?", arrayOf(email))
        if (cursorUser.moveToFirst()) {
            val userId = cursorUser.getInt(0)
            val existingEmail = cursorUser.getString(1)
            val existingPassword = cursorUser.getString(2)
            if (email == existingEmail && password == existingPassword) {
                cursorUser.close()
                updateUserLoggedIn(userId)
                return true
            }
        }
        cursorUser.close()
        return false
    }

    private fun checkUserEmail(email: String): Boolean {
        var isEmailAvailable: Boolean = false
        val db = this.readableDatabase
        val cursorUser = db.rawQuery("SELECT * FROM $USER WHERE email = ?", arrayOf(email))
        if (cursorUser.moveToFirst()) {
            if (email == cursorUser.getString(2)) {
                isEmailAvailable = true
            }
        }
        cursorUser.close()
        return isEmailAvailable
    }

    fun getUserById(id: Int) : User? {
        var user: User? = null
        val db = this.readableDatabase
        val cursorUser = db.rawQuery("SELECT * FROM $USER WHERE id = ?", arrayOf(id.toString()))
        if (cursorUser.moveToFirst()) {
            user = User(
                cursorUser.getInt(0),
                cursorUser.getString(1),
                cursorUser.getString(2),
                null,
                cursorUser.getInt(4),
                cursorUser.getInt(5),
                cursorUser.getInt(6),
                cursorUser.getString(7),
                cursorUser.getInt(8)
            )
        }
        cursorUser.close()
        return user
    }

    fun getCurrentUserId() : Int? {
        var id: Int? = null
        val db = this.readableDatabase
        val cursorUser = db.rawQuery("SELECT id FROM $USER WHERE is_logged_in = ?", arrayOf("1"))
        if (cursorUser.moveToFirst()) {
            id = cursorUser.getInt(0)
        }
        cursorUser.close()
        return id
    }

    fun updateUser(id: Int, name: String, age: Int, weight: Int, height: Int, gender: String) {
        val values = ContentValues()
        values.put("name", name)
        values.put("age", age)
        values.put("weight", weight)
        values.put("height", height)
        values.put("gender", gender)

        val db = this.writableDatabase
        db.update(USER, values, "id = ?", arrayOf(id.toString()))
        db.close()
    }

    private fun updateUserLoggedIn(id: Int) {
        val values = ContentValues()
        values.put("is_logged_in", 1)
        val db = this.writableDatabase
        db.update(USER, values, "id = ?", arrayOf(id.toString()))
        db.close()
    }

    fun logout(id: Int) {
        val values = ContentValues()
        values.put("is_logged_in", 0)
        val db = this.writableDatabase
        db.update(USER, values, "id = ?", arrayOf(id.toString()))
        db.close()
    }

    /* FOOD ======================================================================================*/

    fun addFood(food: Food) {
        val values = ContentValues()
        values.put("id", food.id)
        values.put("name", food.name)
        values.put("calories", food.calories)
        values.put("carbohydrate", food.carbohydrate)
        values.put("proteins", food.proteins)
        values.put("fat", food.fat)

        val db = this.writableDatabase
        db.insert(FOOD, null, values)
        db.close()
    }

    fun getAllFood(): ArrayList<Food> {
        val db = this.readableDatabase
        val foodArrayList = ArrayList<Food>()
        val cursorFood = db.rawQuery("SELECT * FROM $FOOD", null)
        if (cursorFood.moveToFirst()) {
            do {
                foodArrayList.add(
                    Food(
                        cursorFood.getInt(0),
                        cursorFood.getString(1),
                        cursorFood.getDouble(2),
                        cursorFood.getDouble(3),
                        cursorFood.getDouble(4),
                        cursorFood.getDouble(5),
                    )
                )
            } while (cursorFood.moveToNext())
        }
        cursorFood.close()
        return foodArrayList
    }

    fun getFoodById(id: Int) : Food? {
        var food: Food? = null
        val db = this.readableDatabase
        val cursorFood = db.rawQuery("SELECT * FROM $FOOD WHERE id = ?", arrayOf(id.toString()))
        if (cursorFood.moveToFirst()) {
            food = Food(
                cursorFood.getInt(0),
                cursorFood.getString(1),
                cursorFood.getDouble(2),
                cursorFood.getDouble(3),
                cursorFood.getDouble(4),
                cursorFood.getDouble(5),
            )
        }
        cursorFood.close()
        return food
    }

    fun getFoodByKeyword(keyword: String): ArrayList<Food> {
        val db = this.readableDatabase
        val foodArrayList = ArrayList<Food>()
        val cursorFood = db.rawQuery("SELECT * FROM $FOOD WHERE name LIKE ?", arrayOf("%$keyword%"))
        if (cursorFood.moveToFirst()) {
            do {
                foodArrayList.add(
                    Food(
                        cursorFood.getInt(0),
                        cursorFood.getString(1),
                        cursorFood.getDouble(2),
                        cursorFood.getDouble(3),
                        cursorFood.getDouble(4),
                        cursorFood.getDouble(5),
                    )
                )
            } while (cursorFood.moveToNext())
        }
        cursorFood.close()
        return foodArrayList
    }

    /* EATING HISTORY ============================================================================*/
    fun addEatingHistory(eatingHistory: EatingHistory) {
        val values = ContentValues()
        values.put("user_id", eatingHistory.userId)
        values.put("date", eatingHistory.date)
        values.put("food_id", eatingHistory.foodId)
        values.put("food_weight", eatingHistory.foodWeight)

        val db = this.writableDatabase
        db.insert(EATING_HISTORY, null, values)
        db.close()
    }

    fun deleteEatingHistoryById(id: Int, foodId: Int, userId: Int): Boolean {
        val db = this.writableDatabase
        val food = getFoodById(foodId)
        val eatingHistory = getEatingHistoryById(id)
        if (eatingHistory != null && food != null) {
            db.delete(EATING_HISTORY, "id = ?", arrayOf(id.toString()))
            updateCalorieDiariesProgressMinus(
                userId,
                StringDate().getCurrentDate(),
                food.calories.times(eatingHistory.foodWeight),
                food.carbohydrate.times(eatingHistory.foodWeight),
                food.proteins.times(eatingHistory.foodWeight),
                food.fat.times(eatingHistory.foodWeight),
            )
            return true
        }
        db.close()
        return false
    }

    private fun getEatingHistoryById(id: Int): EatingHistory? {
        val db = this.readableDatabase
        var eatingHistory: EatingHistory? = null
        val cursorEatingHistory = db.rawQuery("SELECT * FROM $EATING_HISTORY WHERE id = ?", arrayOf(id.toString()))
        if (cursorEatingHistory.moveToFirst()) {
            eatingHistory = EatingHistory(
                cursorEatingHistory.getInt(0),
                cursorEatingHistory.getInt(1),
                cursorEatingHistory.getString(2),
                cursorEatingHistory.getInt(3),
                cursorEatingHistory.getInt(4)
            )
        }
        cursorEatingHistory.close()
        return eatingHistory
    }

    fun getTodayEatingHistoryByDate(userId: Int, date: String): ArrayList<TodayEatingHistory> {
        val db = this.readableDatabase
        val todayEatingHistoryArrayList = ArrayList<TodayEatingHistory>()
        val cursorEatingHistory = db.rawQuery(
            "SELECT e.id AS id," +
                    "f.id AS food_id," +
                    "f.name AS name, " +
                    "e.food_weight AS food_weight " +
                    "FROM $EATING_HISTORY AS e " +
                    "INNER JOIN $FOOD AS f ON e.food_id = f.id " +
                    "WHERE e.user_id = ? AND e.date = ?",
            arrayOf(userId.toString(), date))
        if (cursorEatingHistory.moveToFirst()) {
            do {
                todayEatingHistoryArrayList.add(
                    TodayEatingHistory(
                        cursorEatingHistory.getInt(0),
                        cursorEatingHistory.getInt(1),
                        cursorEatingHistory.getString(2),
                        cursorEatingHistory.getInt(3)
                    )
                )
            } while (cursorEatingHistory.moveToNext())
        }
        cursorEatingHistory.close()
        return todayEatingHistoryArrayList
    }

    /* CALORIE DIARIES ===========================================================================*/
    fun addCalorieDiaries(calorieDiaries: CalorieDiaries) {
        val values = ContentValues()
        values.put("user_id", calorieDiaries.userId)
        values.put("date", calorieDiaries.date)
        values.put("progress_calories", calorieDiaries.progressCalories)
        values.put("max_calories", calorieDiaries.maxCalories)
        values.put("progress_carbohydrate", calorieDiaries.progressCarbohydrate)
        values.put("max_carbohydrate", calorieDiaries.maxCarbohydrate)
        values.put("progress_proteins", calorieDiaries.progressProteins)
        values.put("max_proteins", calorieDiaries.maxProteins)
        values.put("progress_fat", calorieDiaries.progressFat)
        values.put("max_fat", calorieDiaries.maxFat)

        val db = this.writableDatabase
        db.insert(CALORIE_DIARIES, null, values)
        db.close()
    }

    fun getCalorieDiariesByDate(userId: Int, date: String): CalorieDiaries? {
        var calorieDiaries: CalorieDiaries? = null
        val db = this.readableDatabase
        val cursorCalorieDiaries = db.rawQuery("SELECT * FROM $CALORIE_DIARIES WHERE user_id = ? AND date = ?",
            arrayOf(userId.toString(), date))
        if (cursorCalorieDiaries.moveToFirst()) {
            calorieDiaries = CalorieDiaries(
                cursorCalorieDiaries.getInt(0),
                cursorCalorieDiaries.getString(1),
                cursorCalorieDiaries.getDouble(2),
                cursorCalorieDiaries.getDouble(3),
                cursorCalorieDiaries.getDouble(4),
                cursorCalorieDiaries.getDouble(5),
                cursorCalorieDiaries.getDouble(6),
                cursorCalorieDiaries.getDouble(7),
                cursorCalorieDiaries.getDouble(8),
                cursorCalorieDiaries.getDouble(9),
            )
        }
        cursorCalorieDiaries.close()
        return calorieDiaries
    }

    fun updateCalorieDiariesProgress(userId: Int, date: String, calories: Double, carbohydrate: Double, proteins: Double, fat: Double) {
        val todayCalorieDiaries = getCalorieDiariesByDate(userId, date)
        if (todayCalorieDiaries != null) {
            val values = ContentValues()
            values.put("progress_calories", todayCalorieDiaries.progressCalories + calories)
            values.put("progress_carbohydrate", todayCalorieDiaries.progressCarbohydrate + carbohydrate)
            values.put("progress_proteins", todayCalorieDiaries.progressProteins + proteins)
            values.put("progress_fat", todayCalorieDiaries.progressFat + fat)

            val db = this.writableDatabase
            db.update(CALORIE_DIARIES, values, "user_id = ? AND date = ?", arrayOf(userId.toString(), date))
            db.close()
        }
    }

    private fun updateCalorieDiariesProgressMinus(userId: Int, date: String, calories: Double, carbohydrate: Double, proteins: Double, fat: Double) {
        val todayCalorieDiaries = getCalorieDiariesByDate(userId, date)
        if (todayCalorieDiaries != null) {
            val values = ContentValues()
            values.put("progress_calories", todayCalorieDiaries.progressCalories - calories)
            values.put("progress_carbohydrate", todayCalorieDiaries.progressCarbohydrate - carbohydrate)
            values.put("progress_proteins", todayCalorieDiaries.progressProteins - proteins)
            values.put("progress_fat", todayCalorieDiaries.progressFat - fat)

            val db = this.writableDatabase
            db.update(CALORIE_DIARIES, values, "user_id = ? AND date = ?", arrayOf(userId.toString(), date))
            db.close()
        }
    }

    fun updateCalorieDiariesMax(id: Int, date: String, maxCalories: Double, maxCarbohydrate: Double, maxProteins: Double, maxFat: Double) {
        val values = ContentValues()
        values.put("max_calories", maxCalories)
        values.put("max_carbohydrate", maxCarbohydrate)
        values.put("max_proteins", maxProteins)
        values.put("max_fat", maxFat)

        val db = this.writableDatabase
        db.update(CALORIE_DIARIES, values, "user_id = ? AND date = ?", arrayOf(id.toString(), date))
        db.close()
    }
}