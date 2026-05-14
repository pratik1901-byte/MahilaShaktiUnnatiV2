package com.example.mahilashaktiunnativ2.ui

data class TopicSection(

    val heading: String,

    val content: String
)

data class FinanceTopic(

    val title: String,

    val shortDescription: String,

    val sections: List<TopicSection>
)