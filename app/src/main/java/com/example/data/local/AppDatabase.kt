package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        WalkingSessionEntity::class,
        LocationBreadcrumbEntity::class,
        WalletLedgerEntity::class,
        PandalEntity::class,
        RedemptionEntity::class,
        AdminConfigEntity::class,
        AdminAuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun walkingDao(): WalkingDao
    abstract fun walletDao(): WalletDao
    abstract fun pandalDao(): PandalDao
    abstract fun redemptionDao(): RedemptionDao
    abstract fun adminDao(): AdminDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pujowalk_rewards.db"
                )
                .addCallback(DatabaseCallback())
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        seedDatabase(database)
                    }
                }
            }
        }

        suspend fun seedDatabase(db: AppDatabase) {
            // Seed User
            db.userDao().insertUser(
                UserEntity(
                    id = 1L,
                    name = "সৌমিক চ্যাটার্জী",
                    phone = "+91 98300 12345",
                    email = "soumik.kolkata@example.com",
                    isPhoneVerified = true,
                    isTermsAccepted = true,
                    selectedLanguage = "bn",
                    role = "USER",
                    createdAt = System.currentTimeMillis() - 86400000L * 2
                )
            )

            // Seed Admin Configs
            val configs = listOf(
                AdminConfigEntity("promo_active", "true", "Pujo 10 KM Challenge active status"),
                AdminConfigEntity("promo_start", "2026-10-01", "Promotional start date"),
                AdminConfigEntity("promo_end", "2026-10-31", "Promotional end date"),
                AdminConfigEntity("daily_cap_km", "10", "Maximum eligible daily walking distance in km"),
                AdminConfigEntity("bonus_amount", "5", "Special continuous 10km bonus reward in INR"),
                AdminConfigEntity("budget_total", "100000", "Total allocated company reward budget"),
                AdminConfigEntity("emergency_pause", "false", "Emergency switch to pause reward accrual")
            )
            for (c in configs) {
                db.adminDao().setConfig(c)
            }

            // Seed Audit Log
            db.adminDao().insertAuditLog(
                AdminAuditLogEntity(
                    adminName = "System",
                    action = "INITIALIZE",
                    details = "PujoWalk Rewards database seeded with Kolkata pandals and challenge rules"
                )
            )

            // Seed Verified Kolkata Pandals
            val pandals = listOf(
                PandalEntity(
                    id = 1L,
                    nameBn = "বাগবাজার সর্বজনীন দুর্গোৎসব",
                    nameEn = "Bagbazar Sarbojanin Durgotsav",
                    addressBn = "৭/১, বাগবাজার স্ট্রিট, উত্তর কলকাতা",
                    addressEn = "7/1 Bagbazar Street, North Kolkata",
                    locality = "বাগবাজার (Bagbazar)",
                    zone = "North Kolkata",
                    latitude = 22.6033,
                    longitude = 88.3683,
                    descriptionBn = "শতবর্ষ প্রাচীন ঐতিহ্যবাহী একচালা খাঁটি ডাকের সাজের প্রতিমা। কলকাতার অন্যতম শ্রদ্ধেয় পূজা।",
                    descriptionEn = "Over a century old historic puja famed for traditional 'Ekchala Daker Saaj' Pratima and cultural heritage.",
                    highlights = "Traditional Daker Saaj, 100+ years heritage, Ganga Ghat proximity",
                    visitingHours = "২৪ ঘণ্টা খোলা (24 Hours)",
                    isDemonstrationData = false,
                    isApproved = true,
                    isActive = true
                ),
                PandalEntity(
                    id = 2L,
                    nameBn = "শোভাবাজার রাজবাড়ি দুর্গোৎসব",
                    nameEn = "Sovabazar Rajbari Durga Puja",
                    addressBn = "৩৬, নবকৃষ্ণ দেব স্ট্রিট, শোভাবাজার",
                    addressEn = "36 Nabakrishna Deb Street, Sovabazar",
                    locality = "শোভাবাজার (Sovabazar)",
                    zone = "North Kolkata",
                    latitude = 22.5978,
                    longitude = 88.3653,
                    descriptionBn = "১৭৫৭ সালে রাজা নবকৃষ্ণ দেব কর্তৃক প্রতিষ্ঠিত ঐতিহাসিক নাটমন্দিরের রাজকীয় বনেদি পূজা।",
                    descriptionEn = "Historic royal heritage puja started in 1757 at the palatial Natmandir by Raja Nabakrishna Deb.",
                    highlights = "Royal heritage architecture, Natmandir rituals, aristocratic history",
                    visitingHours = "সকাল ৬টা - রাত ১২টা (6 AM - 12 AM)",
                    isDemonstrationData = false,
                    isApproved = true,
                    isActive = true
                ),
                PandalEntity(
                    id = 3L,
                    nameBn = "কলেজ স্কয়ার সর্বজনীন",
                    nameEn = "College Square Sarbojanin",
                    addressBn = "৫৩, কলেজ স্ট্রিট, বইপাড়া, মধ্য কলকাতা",
                    addressEn = "53 College Street, Boi Para, Central Kolkata",
                    locality = "কলেজ স্ট্রিট (College Street)",
                    zone = "Central Kolkata",
                    latitude = 22.5744,
                    longitude = 88.3639,
                    descriptionBn = "জলের ওপর আলোর রাজকীয় মায়াবী প্রতিবিম্ব ও সুবিশাল স্মৃতিস্তম্ভ প্রতিরূপ প্যান্ডেল।",
                    descriptionEn = "World-famous illuminations reflecting over the pristine lake with grand architectural replicas.",
                    highlights = "Lake water reflection illumination, massive temple architecture",
                    visitingHours = "২৪ ঘণ্টা খোলা (24 Hours)",
                    isDemonstrationData = false,
                    isApproved = true,
                    isActive = true
                ),
                PandalEntity(
                    id = 4L,
                    nameBn = "সন্তোষ মিত্র স্কয়ার",
                    nameEn = "Santosh Mitra Square",
                    addressBn = "লেবুতলা পার্ক, শিয়ালদহ সংলগ্ন, মধ্য কলকাতা",
                    addressEn = "Lebutala Park, Sealdah, Central Kolkata",
                    locality = "শিয়ালদহ (Sealdah)",
                    zone = "Central Kolkata",
                    latitude = 22.5684,
                    longitude = 88.3667,
                    descriptionBn = "চমকপ্রদ থিম, আধুনিক লেজার ও আলোর প্রদর্শনী এবং নয়নাভিরাম স্থাপত্য কলা।",
                    descriptionEn = "Renowned for ground-breaking technological architecture, laser displays, and grand monuments.",
                    highlights = "Laser show, futuristic lighting, majestic structure",
                    visitingHours = "বেলা ২টো - ভোর ৪টে (2 PM - 4 AM)",
                    isDemonstrationData = false,
                    isApproved = true,
                    isActive = true
                ),
                PandalEntity(
                    id = 5L,
                    nameBn = "একডালিয়া এভারগ্রিন ক্লাব",
                    nameEn = "Ekdalia Evergreen Club",
                    addressBn = "১৫, একডালিয়া রোড, গড়িয়াহাট, দক্ষিণ কলকাতা",
                    addressEn = "15 Ekdalia Road, Gariahat, South Kolkata",
                    locality = "গড়িয়াহাট (Gariahat)",
                    zone = "South Kolkata",
                    latitude = 22.5190,
                    longitude = 88.3650,
                    descriptionBn = "দক্ষিণ ভারতের ঐতিহ্যবাহী মন্দিরের নিখুঁত প্রতিরূপ, রাজকীয় ঝাড়বাতি ও সাবেকি প্রতিমা।",
                    descriptionEn = "Splendid replicas of classical Indian temples, dazzling vintage chandeliers, and pure traditional idol.",
                    highlights = "Exquisite German chandeliers, temple replica, classical ambience",
                    visitingHours = "২৪ ঘণ্টা খোলা (24 Hours)",
                    isDemonstrationData = false,
                    isApproved = true,
                    isActive = true
                ),
                PandalEntity(
                    id = 6L,
                    nameBn = "সিংঘী পার্ক সর্বজনীন",
                    nameEn = "Singhi Park Sarbojanin",
                    addressBn = "ডোভার লেন, গড়িয়াহাট সংলগ্ন, দক্ষিণ কলকাতা",
                    addressEn = "Dover Lane, Gariahat, South Kolkata",
                    locality = "ডোভার লেন (Dover Lane)",
                    zone = "South Kolkata",
                    latitude = 22.5205,
                    longitude = 88.3635,
                    descriptionBn = "৮০ বছরেরও বেশি সময় ধরে সমৃদ্ধ হস্তশিল্প ও সনাতন শিল্পের শ্রেষ্ঠ নিদর্শন।",
                    descriptionEn = "Over 80 years of sublime traditional craftsmanship and artistic temple themes.",
                    highlights = "Pure Bengali craftsmanship, serene spiritual atmosphere",
                    visitingHours = "২৪ ঘণ্টা খোলা (24 Hours)",
                    isDemonstrationData = false,
                    isApproved = true,
                    isActive = true
                ),
                PandalEntity(
                    id = 7L,
                    nameBn = "মুদিয়ালী ক্লাব",
                    nameEn = "Mudiali Club",
                    addressBn = "রাজনি সেন রোড, রবীন্দ্র সরোবর সংলগ্ন, দক্ষিণ কলকাতা",
                    addressEn = "Rajani Sen Road, Rabindra Sarobar, South Kolkata",
                    locality = "রবীন্দ্র সরোবর (Rabindra Sarobar)",
                    zone = "South Kolkata",
                    latitude = 22.5113,
                    longitude = 88.3496,
                    descriptionBn = "পরিবেশবান্ধব ও নান্দনিক থিম ভাবনার পথিকৃৎ, বর্ণিল আলোর অপূর্ব সমাহার।",
                    descriptionEn = "Pioneers of aesthetic environmental artistry and harmonious lighting design.",
                    highlights = "Nature aesthetics, artistic illumination, serene parkway",
                    visitingHours = "২৪ ঘণ্টা খোলা (24 Hours)",
                    isDemonstrationData = false,
                    isApproved = true,
                    isActive = true
                ),
                PandalEntity(
                    id = 8L,
                    nameBn = "ত্রিধারা সম্মিলনী",
                    nameEn = "Tridhara Sammilani",
                    addressBn = "মনোহরপুকুর রোড, রাসবিহারী, দক্ষিণ কলকাতা",
                    addressEn = "Manoharpukur Road, Rashbehari, South Kolkata",
                    locality = "রাসবিহারী (Rashbehari)",
                    zone = "South Kolkata",
                    latitude = 22.5167,
                    longitude = 88.3583,
                    descriptionBn = "তিনটি ধারার অপার্থিব মেলবন্ধন: আধুনিক শিল্পভাবনা, লোকশিল্প ও দেবীর চিরন্তন রূপ।",
                    descriptionEn = "Trio confluence of modern contemporary installation, rural folk crafts, and spiritual devotion.",
                    highlights = "Thought-provoking thematic concepts, intricate folk installations",
                    visitingHours = "বেলা ১২টা - ভোর ৫টা (12 PM - 5 AM)",
                    isDemonstrationData = false,
                    isApproved = true,
                    isActive = true
                ),
                PandalEntity(
                    id = 9L,
                    nameBn = "সুরুচি সংঘ",
                    nameEn = "Suruchi Sangha",
                    addressBn = "ব্লক এম, নিউ আলিপুর, দক্ষিণ কলকাতা",
                    addressEn = "Block M, New Alipore, South Kolkata",
                    locality = "নিউ আলিপুর (New Alipore)",
                    zone = "South Kolkata",
                    latitude = 22.5245,
                    longitude = 88.3364,
                    descriptionBn = "ভারতের বিভিন্ন রাজ্যের সমৃদ্ধ সংস্কৃতি, সঙ্গীত ও হস্তশিল্পের মেলবন্ধনে তৈরি বিশেষ মণ্ডপ।",
                    descriptionEn = "Famed for showcasing diverse Indian cultural heritage, thematic music, and indigenous crafts.",
                    highlights = "State cultural heritage theme, original theme music",
                    visitingHours = "২৪ ঘণ্টা খোলা (24 Hours)",
                    isDemonstrationData = false,
                    isApproved = true,
                    isActive = true
                ),
                PandalEntity(
                    id = 10L,
                    nameBn = "শ্রীভূমি স্পোর্টিং ক্লাব",
                    nameEn = "Sreebhumi Sporting Club",
                    addressBn = "ভিআইপি রোড, লেক টাউন সংলগ্ন, সল্টলেক ও পূর্ব",
                    addressEn = "VIP Road, Lake Town, Salt Lake & East",
                    locality = "লেক টাউন (Lake Town)",
                    zone = "Salt Lake & East",
                    latitude = 22.5960,
                    longitude = 88.4034,
                    descriptionBn = "বিখ্যাত রাজপ্রাসাদের নিখুঁত রূপ ও হীরক-স্বর্ণখচিত দেবী মূর্তির রাজকীয় শোভা।",
                    descriptionEn = "Grand palace architecture replica, dazzling jewellery ornamentations and VIP Road illuminations.",
                    highlights = "Monumental royal facade, jewel embellishments, grand entry corridor",
                    visitingHours = "বেলা ২টো - ভোর ৪টে (2 PM - 4 AM)",
                    isDemonstrationData = false,
                    isApproved = true,
                    isActive = true
                ),
                PandalEntity(
                    id = 11L,
                    nameBn = "এফডি ব্লক সল্টলেক",
                    nameEn = "FD Block Salt Lake",
                    addressBn = "সেক্টর ৩, বিধাননগর, সল্টলেক",
                    addressEn = "Sector 3, Bidhannagar, Salt Lake",
                    locality = "সল্টলেক (Salt Lake)",
                    zone = "Salt Lake & East",
                    latitude = 22.5890,
                    longitude = 88.4120,
                    descriptionBn = "সল্টলেকের সুবিশাল সবুজ প্রাঙ্গণে বৈচিত্রময় থিম ও পারিবারিক সৌহার্দ্যময় উৎসব।",
                    descriptionEn = "Sprawling thematic installation nestled within the greenery of Salt Lake Township.",
                    highlights = "Spacious park layout, peaceful crowd management, artistic pandal",
                    visitingHours = "২৪ ঘণ্টা খোলা (24 Hours)",
                    isDemonstrationData = false,
                    isApproved = true,
                    isActive = true
                ),
                PandalEntity(
                    id = 12L,
                    nameBn = "কল্যাণী আইটিআই মোড় লুমিনাস (নমুনা)",
                    nameEn = "Kalyani ITI More (Demonstration)",
                    addressBn = "আইটিআই মোড়, কল্যাণী, নদীয়া (উপনগরী)",
                    addressEn = "ITI More, Kalyani, Nadia (Suburban)",
                    locality = "কল্যাণী (Kalyani)",
                    zone = "North Suburban",
                    latitude = 22.9750,
                    longitude = 88.4344,
                    descriptionBn = "কলকাতার বাইরের উপনগরী সম্প্রসারণের জন্য পরীক্ষামূলক নমুনা ডেটা। সুউচ্চ আধুনিক স্থাপত্য।",
                    descriptionEn = "Demonstration suburban data for regional service expansion testing.",
                    highlights = "Demonstration testing record, expansion zone",
                    visitingHours = "বিকাল ৪টে - রাত ২টো (4 PM - 2 AM)",
                    isDemonstrationData = true,
                    isApproved = true,
                    isActive = true
                )
            )
            db.pandalDao().insertPandals(pandals)

            // Seed initial completed walk session & ledger credit
            val sessionTime = System.currentTimeMillis() - 7200000L
            val initialSessionId = db.walkingDao().insertSession(
                WalkingSessionEntity(
                    userId = 1L,
                    startTime = sessionTime,
                    endTime = sessionTime + 3600000L,
                    distanceKm = 4.25,
                    steps = 5520,
                    durationSeconds = 3600L,
                    isContinuous = true,
                    status = "VERIFIED",
                    isSpecialBonusEligible = false,
                    specialBonusAwarded = false,
                    standardRewardInr = 4.0, // 4 verified km = ₹4
                    bonusRewardInr = 0.0,
                    createdAt = sessionTime
                )
            )

            // Insert initial verified credit into wallet ledger
            db.walletDao().insertTransaction(
                WalletLedgerEntity(
                    userId = 1L,
                    type = "CREDIT",
                    category = "WALK_STANDARD",
                    amountInr = 4.0,
                    description = "Verified Walking Reward (4.25 km)",
                    referenceId = "SESSION-$initialSessionId",
                    status = "APPROVED",
                    createdAt = sessionTime
                )
            )
        }
    }
}
