package com.foxden.fitnessapp.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

object Constants {

    enum class ActivityIcons(val image: ImageVector, val displayText: String = "") {
        DIRECTIONS_BOAT(Icons.Outlined.DirectionsBoat, "Boat"),
        FITNESS_CENTER(Icons.Outlined.FitnessCenter, "Gym"),
        DIRECTIONS_RUN(Icons.Outlined.DirectionsRun, "Running"),
        DIRECTIONS_WALK(Icons.Outlined.DirectionsWalk, "Walking"),
        DIRECTIONS_BIKE(Icons.Outlined.DirectionsBike, "Cycling"),
        HIKING(Icons.Outlined.Hiking, "Hiking"),
        POOL(Icons.Outlined.Pool, "Swimming"),
        PETS(Icons.Outlined.Pets, "Pets"),
        FLIGHT(Icons.Outlined.Flight, "Flight"),
        ACCESSIBLE_FORWARD(Icons.Outlined.AccessibleForward, "Person In Wheelchair"),
        ANCHOR(Icons.Outlined.Anchor, "Boat"),
        ROWING(Icons.Outlined.Rowing, "Rowing"),
        SELF_IMPROVEMENT(Icons.Outlined.SelfImprovement, "Self Improvement"),
        SPORTS_SOCCER(Icons.Outlined.SportsSoccer, "Football"),
        EMOJI_NATURE(Icons.Outlined.EmojiNature, "Gardening"),
        SPORTS_BASKETBALL(Icons.Outlined.SportsBasketball, "Basketball"),
        SPORTS_KABADDI(Icons.Outlined.SportsKabaddi, "Bad Touch"),
        SPORTS(Icons.Outlined.Sports, "Whistle"),
        SPORTS_TENNIS(Icons.Outlined.SportsTennis, "Tennis"),
        SURFING(Icons.Outlined.Surfing, "Surfing Dude"),
        SPORTS_MOTORSPORTS(Icons.Outlined.SportsMotorsports, "Motorsports"),
        SPORTS_HANDBALL(Icons.Outlined.SportsHandball, "Handball"),
        SPORTS_BASEBALL(Icons.Outlined.SportsBaseball, "Baseball"),
        SPORTS_VOLLEYBALL(Icons.Outlined.SportsVolleyball, "Volleyball"),
        SPORTS_FOOTBALL(Icons.Outlined.SportsFootball, "Rugby"),
        DOWNHILL_SKIING(Icons.Outlined.DownhillSkiing, "Skiing"),
        KAYAKING(Icons.Outlined.Kayaking, "Kayaking"),
        SKATEBOARDING(Icons.Outlined.Skateboarding, "Skateboarding"),
        SPORTS_CRICKET(Icons.Outlined.SportsCricket, "Cricket"),
        SPORTS_MARTIAL_ARTS(Icons.Outlined.SportsMartialArts, "Martial Arts"),
        NORDIC_WALKING(Icons.Outlined.NordicWalking, "Nordic Walking"),
        SPORTS_GOLF(Icons.Outlined.SportsGolf, "Golf"),
        PARAGLIDING(Icons.Outlined.Paragliding, "Paragliding"),
        SNOWBOARDING(Icons.Outlined.Snowboarding, "Snowboarding"),
        SPORTS_GYMNASTICS(Icons.Outlined.SportsGymnastics, "Gymnastics"),
        KITESURFING(Icons.Outlined.Kitesurfing, "Kitesurfing"),
        SNOWSHOEING(Icons.Outlined.Snowshoeing, "Snowshoeing"),
        SPORTS_HOCKEY(Icons.Outlined.SportsHockey, "Hockey"),
        ICE_SKATING(Icons.Outlined.IceSkating, "Ice Skating"),
        SPORTS_RUGBY(Icons.Outlined.SportsRugby, "Rugby 2"),
        SLEDDING(Icons.Outlined.Sledding, "Sledding"),
        SCUBA_DIVING(Icons.Outlined.ScubaDiving, "Scuba Diving"),
        ROLLER_SKATING(Icons.Outlined.RollerSkating, "Roller Skating"),
        PARK(Icons.Outlined.Park, "Park"),
        SAILING(Icons.Outlined.Sailing, "Sailing"),
        ELECTRIC_BIKE(Icons.Outlined.ElectricBike, "EBike"),
        SPORTS_SCORE(Icons.Outlined.SportsScore, "Flag"),
        WATER(Icons.Outlined.Water, "Water"),
        SPA(Icons.Outlined.Spa, "Spa"),
        BEACH_ACCESS(Icons.Outlined.BeachAccess, "Beach"),
        GOLF_COURSE(Icons.Outlined.GolfCourse, "Golf 2"),
    }

    enum class Floofer() {
        FOX,
        RACOON,
        CAT
    }

    /*
    val ACTIVITY_TYPE_BUILTIN = listOf<ActivityType>(
        ActivityType(0, "Jogging", ActivityIcons.FITNESS_CENTER.ordinal),
        ActivityType(0, "Hiking", ActivityIcons.FITNESS_CENTER.ordinal),
        ActivityType(0, "Cycling", ActivityIcons.FITNESS_CENTER.ordinal),
    )
     */
}
