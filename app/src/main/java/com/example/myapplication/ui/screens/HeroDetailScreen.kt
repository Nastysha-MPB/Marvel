package com.example.myapplication.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication.R
import com.example.myapplication.presentation.HeroState
import com.example.myapplication.presentation.HeroViewModel
import com.example.myapplication.presentation.HeroViewModelFactory

@Composable
fun HeroDetailScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    heroId: String,
) {
    val viewModel = viewModel<HeroViewModel>(factory = HeroViewModelFactory(heroId))
    val state by viewModel.stateFlow.collectAsState()
    when (val currentState = state) {
        is HeroState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is HeroState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(R.string.error))
            }
        }
        is HeroState.Success -> {
            HeroDetailScreenContent(
                navController = navController,
                modifier = modifier,
                imageUrl = currentState.superhero.imageUrl,
                name = currentState.superhero.name,
                description = currentState.superhero.description,
            )
        }
    }
}

@Composable
fun HeroDetailScreenContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    imageUrl: String,
    name: String,
    description: String,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = rememberAsyncImagePainter(model = imageUrl),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .padding(top = 40.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                border = BorderStroke(0.dp, Color.Transparent),
                modifier = Modifier.size(32.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_left),
                    contentDescription = "Back Button",
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(676.dp))

            Text(
                text = name,
                fontFamily = FontFamily(Font(R.font.inter_28pt_extrabold)),
                fontSize = 38.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = description,
                fontFamily = FontFamily(Font(R.font.inter_28pt_bold)),
                fontSize = 26.sp,
                lineHeight = 36.sp,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun HeroDetailScreenPreview() {
    HeroDetailScreenContent(
        navController = rememberNavController(),
        imageUrl = "https://i.pinimg.com/736x/1d/4f/15/1d4f15fd8b24a421ac403a511fe67451.jpg",
        name = "Charles Xavier",
        description = "Head of X-Men"
    )
}