package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PandalEntity
import com.example.ui.theme.PujoCharcoal
import com.example.ui.theme.PujoCream
import com.example.ui.theme.PujoCrimson
import com.example.ui.theme.PujoCrimsonDark
import com.example.ui.theme.PujoGold
import com.example.ui.theme.PujoGoldDark
import com.example.ui.theme.PujoGoldLight
import com.example.ui.theme.PujoPurple
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningOrange
import com.example.ui.theme.WarningOrangeLight
import com.example.util.AppLanguage
import com.example.util.Strings
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorePandalsScreen(
    pandals: List<PandalEntity>,
    language: AppLanguage,
    userLat: Double = 22.5726,
    userLng: Double = 88.3639,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isMapView by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedZone by remember { mutableStateOf("ALL") }
    var selectedPandalForDetails by remember { mutableStateOf<PandalEntity?>(null) }

    // Filter pandals
    val filteredPandals = remember(pandals, searchQuery, selectedZone) {
        pandals.filter { p ->
            val matchesZone = when (selectedZone) {
                "NORTH" -> p.zone.contains("North", ignoreCase = true)
                "SOUTH" -> p.zone.contains("South", ignoreCase = true)
                "CENTRAL" -> p.zone.contains("Central", ignoreCase = true)
                "EAST" -> p.zone.contains("Salt Lake", ignoreCase = true) || p.zone.contains("East", ignoreCase = true)
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    p.nameBn.contains(searchQuery, ignoreCase = true) ||
                    p.nameEn.contains(searchQuery, ignoreCase = true) ||
                    p.locality.contains(searchQuery, ignoreCase = true) ||
                    p.addressBn.contains(searchQuery, ignoreCase = true) ||
                    p.addressEn.contains(searchQuery, ignoreCase = true)

            matchesZone && matchesSearch
        }.sortedBy { calculateDistanceKm(userLat, userLng, it.latitude, it.longitude) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDF9))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Search Bar and View Mode Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = Strings.get("search_pandal_hint", language),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = PujoCrimson
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .testTag("pandal_search_input"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PujoCrimson,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Map vs List Toggle
            Surface(
                modifier = Modifier
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { isMapView = !isMapView }
                    .testTag("view_mode_toggle_btn"),
                color = if (isMapView) PujoCrimson else PujoCream,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PujoGold.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isMapView) Icons.Default.ViewList else Icons.Default.Map,
                        contentDescription = "Toggle View",
                        tint = if (isMapView) Color.White else PujoCrimsonDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isMapView) Strings.get("list_view", language) else Strings.get("map_view", language),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isMapView) Color.White else PujoCrimsonDark
                        )
                    )
                }
            }
        }

        // Zone Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val zones = listOf(
                Pair("ALL", Strings.get("filter_all", language)),
                Pair("NORTH", Strings.get("filter_north", language)),
                Pair("SOUTH", Strings.get("filter_south", language)),
                Pair("CENTRAL", Strings.get("filter_central", language)),
                Pair("EAST", Strings.get("filter_east", language))
            )
            items(zones) { (key, label) ->
                val isSelected = selectedZone == key
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedZone = key },
                    label = { Text(text = label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PujoCrimson,
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.testTag("filter_chip_$key")
                )
            }
        }

        // Content: Interactive Kolkata Map Canvas OR List View
        if (isMapView) {
            InteractiveKolkataMap(
                pandals = filteredPandals,
                userLat = userLat,
                userLng = userLng,
                language = language,
                onMarkerClick = { selectedPandalForDetails = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filteredPandals.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "কোনো প্যান্ডেল পাওয়া যায়নি। অনুসন্ধান ফিল্টার পরিবর্তন করুন।",
                                style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                            )
                        }
                    }
                } else {
                    items(filteredPandals) { pandal ->
                        val distanceKm = calculateDistanceKm(userLat, userLng, pandal.latitude, pandal.longitude)
                        PandalListItem(
                            pandal = pandal,
                            distanceKm = distanceKm,
                            language = language,
                            onClick = { selectedPandalForDetails = pandal },
                            onDirectionsClick = {
                                openWalkingDirections(context, pandal.latitude, pandal.longitude, pandal.nameEn)
                            }
                        )
                    }
                }
            }
        }
    }

    // Pandal Details Bottom Sheet
    selectedPandalForDetails?.let { pandal ->
        val distanceKm = calculateDistanceKm(userLat, userLng, pandal.latitude, pandal.longitude)
        PandalDetailSheet(
            pandal = pandal,
            distanceKm = distanceKm,
            language = language,
            onDismiss = { selectedPandalForDetails = null },
            onDirectionsClick = {
                openWalkingDirections(context, pandal.latitude, pandal.longitude, pandal.nameEn)
            }
        )
    }
}

@Composable
fun InteractiveKolkataMap(
    pandals: List<PandalEntity>,
    userLat: Double,
    userLng: Double,
    language: AppLanguage,
    onMarkerClick: (PandalEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.testTag("interactive_map_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F5F0)),
        border = androidx.compose.foundation.BorderStroke(1.dp, PujoCream)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(pandals) {
                        detectTapGestures { tapOffset ->
                            // Hit test closest pandal marker within 35 pixels
                            val width = size.width
                            val height = size.height

                            val minLat = 22.50
                            val maxLat = 22.62
                            val minLng = 88.32
                            val maxLng = 88.43

                            val clicked = pandals.firstOrNull { p ->
                                val x = ((p.longitude - minLng) / (maxLng - minLng) * width).toFloat()
                                val y = ((maxLat - p.latitude) / (maxLat - minLat) * height).toFloat()
                                val dx = tapOffset.x - x
                                val dy = tapOffset.y - y
                                (dx * dx + dy * dy) <= (36 * 36)
                            }
                            if (clicked != null) {
                                onMarkerClick(clicked)
                            }
                        }
                    }
            ) {
                val w = size.width
                val h = size.height

                // Kolkata Bounding Box: Lat 22.50 to 22.62, Lng 88.32 to 88.43
                val minLat = 22.50
                val maxLat = 22.62
                val minLng = 88.32
                val maxLng = 88.43

                // 1. Draw Hooghly River curve
                val riverPath = Path()
                riverPath.moveTo(w * 0.28f, 0f)
                riverPath.cubicTo(w * 0.25f, h * 0.3f, w * 0.22f, h * 0.6f, w * 0.18f, h)
                drawPath(
                    path = riverPath,
                    color = Color(0xFFBAE6FD),
                    style = Stroke(width = 24f)
                )

                // 2. Draw Kolkata main arterial roads (Chittaranjan Ave, Gariahat, VIP road)
                drawLine(Color(0xFFE5E7EB), Offset(w * 0.38f, 0f), Offset(w * 0.42f, h), 6f)
                drawLine(Color(0xFFE5E7EB), Offset(w * 0.42f, h * 0.45f), Offset(w * 0.85f, h * 0.25f), 5f)
                drawLine(Color(0xFFE5E7EB), Offset(w * 0.2f, h * 0.7f), Offset(w * 0.8f, h * 0.75f), 5f)

                // 3. User Location Marker
                val userX = ((userLng - minLng) / (maxLng - minLng) * w).toFloat().coerceIn(20f, w - 20f)
                val userY = ((maxLat - userLat) / (maxLat - minLat) * h).toFloat().coerceIn(20f, h - 20f)
                drawCircle(Color(0x330284C7), radius = 22f, center = Offset(userX, userY))
                drawCircle(Color(0xFF0284C7), radius = 8f, center = Offset(userX, userY))
                drawCircle(Color.White, radius = 3f, center = Offset(userX, userY))

                // 4. Pandal Markers
                pandals.forEach { p ->
                    val px = ((p.longitude - minLng) / (maxLng - minLng) * w).toFloat().coerceIn(20f, w - 20f)
                    val py = ((maxLat - p.latitude) / (maxLat - minLat) * h).toFloat().coerceIn(20f, h - 20f)

                    // Draw outer glow and pin
                    drawCircle(
                        color = if (p.isDemonstrationData) WarningOrange.copy(alpha = 0.4f) else PujoCrimson.copy(alpha = 0.3f),
                        radius = 16f,
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = if (p.isDemonstrationData) WarningOrange else PujoCrimson,
                        radius = 9f,
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = PujoGoldLight,
                        radius = 4f,
                        center = Offset(px, py)
                    )
                }
            }

            // Map Overlay Legend
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(10.dp),
                color = Color.White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "কলকাতা প্যান্ডেল ম্যাপ ভিউ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PujoCrimson)
                    )
                    Text(
                        text = "মার্কারে ট্যাপ করে বিস্তারিত দেখুন (${pandals.size} টি প্যান্ডেল)",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = Color.Gray)
                    )
                }
            }
        }
    }
}

@Composable
fun PandalListItem(
    pandal: PandalEntity,
    distanceKm: Double,
    language: AppLanguage,
    onClick: () -> Unit,
    onDirectionsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("pandal_item_${pandal.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = PujoCream,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = pandal.zone,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PujoCrimson
                                )
                            )
                        }

                        if (pandal.isDemonstrationData) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = WarningOrangeLight,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = Strings.get("demo_label", language),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = WarningOrange
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (language == AppLanguage.BENGALI) pandal.nameBn else pandal.nameEn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PujoCharcoal
                        )
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = if (language == AppLanguage.BENGALI) pandal.addressBn else pandal.addressEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray,
                            fontSize = 12.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Distance Badge & Directions Button
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = Color(0xFFFFECEF),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "%.1f km".format(distanceKm),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PujoCrimson
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    IconButton(
                        onClick = onDirectionsClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = "Directions",
                            tint = PujoCrimson
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = pandal.visitingHours,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.DarkGray,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PandalDetailSheet(
    pandal: PandalEntity,
    distanceKm: Double,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onDirectionsClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == AppLanguage.BENGALI) pandal.nameBn else pandal.nameEn,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = PujoCrimson
                        )
                    )
                    Text(
                        text = "${pandal.locality} • ${pandal.zone}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color.Gray
                        )
                    )
                }

                Surface(
                    color = Color(0xFFFFECEF),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "%.1f km দূরে".format(distanceKm),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PujoCrimson
                        )
                    )
                }
            }

            if (pandal.isDemonstrationData) {
                Surface(
                    color = WarningOrangeLight,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = WarningOrange)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "এটি পরিষেবা সম্প্রসারণের জন্য পরীক্ষামূলক ডেমো ডেটা।",
                            style = MaterialTheme.typography.labelSmall.copy(color = WarningOrange, fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }

            Text(
                text = if (language == AppLanguage.BENGALI) pandal.descriptionBn else pandal.descriptionEn,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    color = PujoCharcoal
                )
            )

            // Visiting & Highlights Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = PujoCream)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "${Strings.get("visiting_hours", language)}: ${pandal.visitingHours}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PujoCharcoal
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${Strings.get("highlights", language)}: ${pandal.highlights}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.DarkGray
                        )
                    )
                }
            }

            Button(
                onClick = onDirectionsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("open_directions_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PujoCrimson)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.DirectionsWalk, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = Strings.get("get_directions", language),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0 // Earth radius in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

private fun openWalkingDirections(context: android.content.Context, lat: Double, lng: Double, label: String) {
    try {
        val gmmIntentUri = Uri.parse("google.navigation:q=$lat,$lng&mode=w")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")
        if (mapIntent.resolveActivity(context.packageManager) != null) {
            context.startActivity(mapIntent)
        } else {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng&travelmode=walking"))
            context.startActivity(webIntent)
        }
    } catch (_: Exception) {
        val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng?q=$lat,$lng($label)"))
        context.startActivity(fallbackIntent)
    }
}
