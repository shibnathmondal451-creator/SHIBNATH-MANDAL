package com.example.util

enum class AppLanguage(val code: String, val displayName: String) {
    BENGALI("bn", "বাংলা"),
    ENGLISH("en", "English")
}

object Strings {
    fun get(key: String, lang: AppLanguage): String {
        val entry = dictionary[key] ?: return key
        return if (lang == AppLanguage.BENGALI) entry.bn else entry.en
    }

    private data class Translation(val bn: String, val en: String)

    private val dictionary = mapOf(
        "app_title" to Translation("পুজোওয়াক রিওয়ার্ডস", "PujoWalk Rewards"),
        "app_tagline" to Translation("হাঁটুন বেশি। প্যান্ডেল দেখুন। পুরস্কার জিতুন।", "Walk More. Explore Pandals. Earn Rewards."),
        "nav_home" to Translation("হোম", "Home"),
        "nav_walk" to Translation("হাঁটা", "Walk"),
        "nav_explore" to Translation("প্যান্ডেল", "Pandals"),
        "nav_wallet" to Translation("ওয়ালেট", "Wallet"),
        "nav_profile" to Translation("প্রোফাইল", "Profile"),
        "nav_admin" to Translation("অ্যাডমিন", "Admin"),

        // Dashboard
        "welcome" to Translation("শারদোৎসবের শুভেচ্ছা", "Festive Greetings"),
        "today_distance" to Translation("আজকের হাঁটার দূরত্ব", "Today's Walking Distance"),
        "today_reward_est" to Translation("আজকের সম্ভাব্য পুরস্কার", "Today's Estimated Reward"),
        "today_reward_appr" to Translation("অনুমোদিত পুরস্কার", "Approved Reward"),
        "lifetime_distance" to Translation("মোট হাঁটার দূরত্ব", "Lifetime Distance"),
        "available_balance" to Translation("ব্যবহারযোগ্য ব্যালেন্স", "Available Balance"),
        "pending_balance" to Translation("অপেক্ষমান পুরস্কার", "Pending Rewards"),
        "redeemed_total" to Translation("মোট রিডিম করা হয়েছে", "Total Redeemed"),
        "btn_start_walking" to Translation("হাঁটা শুরু করুন", "Start Walking"),
        "btn_resume" to Translation("চালিয়ে যান", "Resume"),
        "btn_pause" to Translation("বিরতি নিন", "Pause"),
        "btn_stop" to Translation("সমাপ্ত করুন", "End Walk"),
        "nearby_pandals" to Translation("কাছের দুর্গোৎসব প্যান্ডেল", "Nearby Durga Puja Pandals"),
        "view_all" to Translation("সব দেখুন", "View All"),
        "rate_card_title" to Translation("রিওয়ার্ডস রেট চার্ট ও নিয়মাবলী", "Rewards Rate Card & Rules"),
        "rate_card_desc" to Translation("প্রতি ভেরিফায়েড কিলোমিটারে ₹১ (সর্বোচ্চ ১০ কিমি/দিন)। ১০ কিমি একনাগাড়ে সম্পূর্ণ করলে বাড়তি ₹৫ পুজো বোনাস (মোট ₹১৫)!", "₹1 per verified km (up to 10 km/day). Complete 10 km continuously for an extra ₹5 Pujo Bonus (₹15 Total)!"),

        // Challenge
        "challenge_title" to Translation("পুজো ১০ কিমি চ্যালেঞ্জ", "Pujo 10 KM Challenge"),
        "challenge_subtitle" to Translation("সীমিত সময়ের শারদীয় অফার", "Limited-Time Festive Offer"),
        "challenge_rule" to Translation("একনাগাড়ে ১০ কিমি হাঁটলেই অতিরিক্ত ₹৫ বোনাস! (মোট ₹১৫)", "Walk 10 km continuously in one eligible session to unlock ₹5 Bonus! (Total ₹15)"),
        "challenge_progress" to Translation("চ্যালেঞ্জের অগ্রগতি", "Challenge Progress"),
        "continuous_session_only" to Translation("শুধুমাত্র একক বিরতিহীন সেশনে প্রযোজ্য", "Applies to a single continuous session only"),

        // Walking Session
        "walking_session" to Translation("লাইভ হাঁটার সেশন", "Live Walking Session"),
        "gps_status" to Translation("জিপিএস স্ট্যাটাস", "GPS Status"),
        "gps_active" to Translation("জিপিএস চালু আছে (নির্ভুল)", "GPS Active (Accurate)"),
        "gps_permission_needed" to Translation("লোকেশন অনুমতি প্রয়োজন", "Location Permission Needed"),
        "grant_permission" to Translation("অনুমতি দিন", "Grant Permission"),
        "speed_kmh" to Translation("গতি", "Speed"),
        "duration" to Translation("সময়", "Duration"),
        "steps" to Translation("পদক্ষেপ", "Steps"),
        "live_earned" to Translation("অর্জিত", "Earned"),
        "walking_mode" to Translation("হাঁটার মোড", "Walking Mode"),
        "outdoor_gps" to Translation("আসল জিপিএস ওয়াক", "Real GPS Walk"),
        "sim_route" to Translation("কলকাতা প্যান্ডেল রুট ট্রায়াল", "Kolkata Pandal Route Trial"),

        // Pandals
        "search_pandal_hint" to Translation("প্যান্ডেল, এলাকা বা জোন খুঁজুন...", "Search pandal, area, or zone..."),
        "filter_all" to Translation("সকল", "All"),
        "filter_north" to Translation("উত্তর কলকাতা", "North Kolkata"),
        "filter_south" to Translation("দক্ষিণ কলকাতা", "South Kolkata"),
        "filter_central" to Translation("মধ্য কলকাতা", "Central Kolkata"),
        "filter_east" to Translation("সল্টলেক ও পূর্ব", "Salt Lake & East"),
        "map_view" to Translation("ম্যাপ ভিউ", "Map View"),
        "list_view" to Translation("তালিকা", "List View"),
        "get_directions" to Translation("দিকনির্দেশ পান", "Get Directions"),
        "visiting_hours" to Translation("দর্শনের সময়", "Visiting Hours"),
        "highlights" to Translation("প্রধান আকর্ষণ", "Highlights"),
        "demo_label" to Translation("পরীক্ষামূলক ডেমো ডেটা", "Demonstration Data"),

        // Wallet & Redeem
        "wallet_title" to Translation("রিওয়ার্ডস ওয়ালেট", "Rewards Wallet"),
        "recent_txns" to Translation("সাম্প্রতিক লেনদেন", "Recent Transactions"),
        "btn_redeem" to Translation("রিডিম করুন", "Redeem Rewards"),
        "select_payout_method" to Translation("পেমেন্ট পদ্ধতি নির্বাচন করুন", "Select Redemption Method"),
        "upi_payout" to Translation("ইউপিআই পেআউট (UPI)", "UPI Instant Payout"),
        "bank_transfer" to Translation("ব্যাংক ট্রান্সফার (NEFT/IMPS)", "Bank Account Transfer"),
        "mobile_recharge" to Translation("মোবাইল রিচার্জ", "Mobile Recharge"),
        "play_card" to Translation("Google Play গিফট কার্ড", "Google Play Gift Card"),
        "flipkart_card" to Translation("Flipkart গিফট কার্ড", "Flipkart Gift Card"),
        "amazon_card" to Translation("Amazon গিফট কার্ড", "Amazon Gift Card"),
        "min_amount" to Translation("সর্বনিম্ন রিডিম", "Minimum Redemption"),
        "enter_details" to Translation("বিবরণ প্রদান করুন", "Enter Payout Details"),
        "confirm_redeem" to Translation("রিডিম অনুরোধ জমা দিন", "Submit Redemption"),
        "insufficient_balance" to Translation("অপর্যাপ্ত ব্যালেন্স!", "Insufficient balance!"),

        // Admin
        "admin_title" to Translation("অ্যাডমিনিস্ট্রেটর পোর্টাল", "Administrator Dashboard"),
        "admin_auth" to Translation("অ্যাডমিন লগইন", "Admin Login"),
        "budget_total" to Translation("মোট রিওয়ার্ডস বাজেট", "Total Rewards Budget"),
        "total_liability" to Translation("বর্তমান দায়বদ্ধতা", "Total Reward Liability"),
        "emergency_pause" to Translation("জরুরী স্থগিতাদেশ (Emergency Pause)", "Emergency Pause Switch"),
        "active_users" to Translation("দৈনিক সক্রিয় ব্যবহারকারী", "Daily Active Users"),
        "total_distance_stat" to Translation("সর্বমোট হাঁটা দূরত্ব", "Total Distance Walked"),
        "pandal_mgmt" to Translation("প্যান্ডেল পরিচালনা", "Pandal Management"),
        "redemption_mgmt" to Translation("রিডেম্পশন অনুরোধ", "Redemptions"),
        "audit_logs" to Translation("অডিট লগ", "Audit Logs"),

        // Legal & Auth
        "register_title" to Translation("রেজিস্ট্রেশন ও লগইন", "Registration & Login"),
        "login_title" to Translation("লগইন", "Login"),
        "create_account" to Translation("নতুন অ্যাকাউন্ট তৈরি", "Create Account"),
        "btn_login" to Translation("লগইন করুন", "Log In"),
        "btn_logout" to Translation("লগআউট", "Log Out"),
        "switch_account" to Translation("অ্যাকাউন্ট পরিবর্তন", "Switch Account"),
        "demo_login" to Translation("ডেমো ইউজার দিয়ে তাৎক্ষণিক লগইন", "Quick Demo Login"),
        "google_login" to Translation("Google দিয়ে সাইন ইন", "Sign in with Google"),
        "phone_number" to Translation("মোবাইল নম্বর", "Mobile Number"),
        "send_otp" to Translation("ওটিপি পাঠান", "Send OTP"),
        "verify_otp" to Translation("ওটিপি যাচাই করুন", "Verify OTP"),
        "user_name" to Translation("আপনার নাম", "Your Full Name"),
        "terms_consent" to Translation("শর্তাবলী ও লোকেশন ট্র্যাকিংয়ে সম্মত", "Agree to Terms & Location Tracking"),
        "terms_title" to Translation("শর্তাবলী ও গোপনীয়তা নীতি", "Terms & Privacy Policy")
    )
}
