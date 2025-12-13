package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

data class CarouselSlide(
    val imageResource: DrawableResource,
    val title: String,
    val description: String
)

@Composable
fun Carousel(
    slides: List<CarouselSlide>,
    modifier: Modifier,
    onComplete: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Image Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) { page ->
            CarouselSlideItem(slide = slides[page])
        }

        // Page Indicators
        PageIndicators(
            pageCount = slides.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier.padding(16.dp)
        )

        // Navigation Buttons
        PagerNavigation(
            currentPage = pagerState.currentPage,
            totalPages = slides.size,
            onPrevious = {
                scope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onNext = {
                if (pagerState.currentPage < slides.size - 1) {
                    scope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                } else {
                    onComplete()
                }
            },
            onSkip = onComplete,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        )
    }
}

@Composable
fun CarouselSlideItem(slide: CarouselSlide) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title
        Text(
            text = slide.title,
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(vertical = 16.dp),

        )
        // Image
        Card(Modifier.height(320.dp)){
            Image(
                painter = painterResource(slide.imageResource),
                contentDescription = slide.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Description
        Text(
            text = slide.description,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun PageIndicators(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        color = if (index == currentPage) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
            )
        }
    }
}

@Composable
fun PagerNavigation(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous Button (only show if not on first page)
        if (currentPage > 0) {
            Button(
                onClick = onPrevious,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                elevation = null
            ) {
                Text("Previous")
            }
        }
        Spacer(Modifier.weight(1f))
//        else {
//            // Skip Button (only on first page)
//            Button(
//                onClick = onSkip,
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Transparent,
//                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
//                ),
//                elevation = null
//            ) {
//                Text("Skip")
//            }
//        }

        // Next/Get Started Button

//        Button(
//            onClick = onNext,
//            modifier = Modifier.width(120.dp)
//        ) {
//            Text(
//                text = if (currentPage < totalPages - 1) "Next" else "Get Started"
//            )
//        }
        if (currentPage < totalPages - 1) Button(
            onClick = onNext,
            modifier = Modifier.width(120.dp)
        ) {
            Text(
                text =  "Next"
            )
        }
    }
}