package com.example.mahilashaktiunnativ2.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mahilashaktiunnativ2.R
import com.example.mahilashaktiunnativ2.ui.theme.*

@Composable
fun LearnScreen() {

    val context = LocalContext.current

    var searchText by remember {

        mutableStateOf("")
    }

    var selectedTopic by remember {

        mutableStateOf<FinanceTopic?>(null)
    }

    val topics = financeTopics

    val filteredTopics = topics.filter {

        it.title.contains(
            searchText,
            ignoreCase = true
        )
    }

    if (selectedTopic != null) {

        TopicDetailScreen(

            topic = selectedTopic!!,

            onBack = {

                selectedTopic = null
            }
        )

    } else {

        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground)
                .padding(16.dp)

        ) {

            Text(

                text = stringResource(R.string.happy_learning),

                style =
                    MaterialTheme
                        .typography
                        .headlineSmall
            )

            Spacer(
                modifier =
                    Modifier.height(6.dp)
            )

            Text(

                text =
                    "Learn finance & banking concepts easily.",

                color = SecondaryText
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            OutlinedTextField(

                value = searchText,

                onValueChange = {

                    searchText = it
                },

                modifier =
                    Modifier.fillMaxWidth(),

                placeholder = {

                    Text(
                        stringResource(R.string.search_topics)
                    )
                },

                leadingIcon = {

                    Icon(
                        Icons.Default.Search,
                        contentDescription = null
                    )
                },

                shape =
                    RoundedCornerShape(18.dp),

                singleLine = true
            )

            Spacer(
                modifier =
                    Modifier.height(20.dp)
            )

            if (filteredTopics.isEmpty()) {

                Column {

                    Text(
                        text =
                            stringResource(R.string.no_results)
                    )

                    Spacer(
                        modifier =
                            Modifier.height(8.dp)
                    )

                    Text(

                        text =
                            stringResource(R.string.try_google),

                        color = PrimaryGreen,

                        modifier = Modifier.clickable {

                            val intent = Intent(

                                Intent.ACTION_VIEW,

                                Uri.parse(
                                    "https://www.google.com/search?q=$searchText"
                                )
                            )

                            context.startActivity(intent)
                        }
                    )
                }

            } else {

                LazyColumn(

                    verticalArrangement =
                        Arrangement.spacedBy(14.dp)

                ) {

                    items(filteredTopics) { topic ->

                        TopicCard(

                            topic = topic,

                            onClick = {

                                selectedTopic = topic
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopicCard(

    topic: FinanceTopic,

    onClick: () -> Unit

) {

    Card(

        modifier =
            Modifier.clickable {

                onClick()
            },

        shape =
            RoundedCornerShape(22.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    CardBackground
            )

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)

        ) {

            Text(

                text = topic.title,

                style =
                    MaterialTheme
                        .typography
                        .titleLarge
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Text(
                text = topic.shortDescription
            )
        }
    }
}

@Composable
fun TopicDetailScreen(

    topic: FinanceTopic,

    onBack: () -> Unit

) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(16.dp)
            .verticalScroll(
                rememberScrollState()
            )

    ) {

        Button(

            onClick = {

                onBack()
            }

        ) {

            Text("Back")
        }

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        Text(

            text = topic.title,

            style =
                MaterialTheme
                    .typography
                    .headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        Column(

            verticalArrangement =
                Arrangement.spacedBy(14.dp)

        ) {

            topic.sections.forEach { section ->

                LearningSection(

                    title = section.heading,

                    content = section.content
                )
            }
        }
    }
}

@Composable
fun LearningSection(

    title: String,

    content: String

) {

    Card(

        shape =
            RoundedCornerShape(20.dp),

        colors =
            CardDefaults.cardColors(
                containerColor =
                    CardBackground
            ),

        modifier =
            Modifier.fillMaxWidth()

    ) {

        Column(

            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)

        ) {

            Text(

                text = title,

                style =
                    MaterialTheme
                        .typography
                        .titleLarge
            )

            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )

            Text(
                text = content
            )
        }
    }
}