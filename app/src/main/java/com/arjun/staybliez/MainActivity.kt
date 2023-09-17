package com.arjun.staybliez

import android.media.MediaMetadataRetriever
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arjun.staybliez.ui.theme.StaybliezTheme
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class MainActivity : ComponentActivity() {
    private lateinit var videoFile: File
    private lateinit var media: MediaMetadataRetriever

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val raw = resources.openRawResource(R.raw.sample)
        videoFile = File(filesDir.path + "/sample.mp4")
        Files.copy(raw, videoFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        media = MediaMetadataRetriever().apply { setDataSource(videoFile.path) }

        setContent {
            val index = remember { mutableStateOf(0) }
            StaybliezTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Column(
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AppBar()

                        Column(
                            modifier = Modifier.padding(10.dp, 0.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("---- Selected Frame No: ${index.value + 1} ----")
                            Spacer(Modifier.height(15.dp))
                            Image(media.getFrameAtIndex(index.value)?.asImageBitmap()!!, "")
                            Spacer(Modifier.height(15.dp))
                            Button(onClick = { /*TODO*/ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_edit),
                                    contentDescription = "Edit Frame"
                                )
                                Text(
                                    "Edit Frame",
                                    color = Color.White,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        VideoFramesList(media) { index.value = it }
                    }
                }
            }
        }
    }
}

@Composable
fun AppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(Color.DarkGray),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_menu),
            null,
            tint = Color.Unspecified,
            modifier = Modifier
                .padding(10.dp, 0.dp)
                .size(30.dp)
        )
        Text("Staybliez", style = TextStyle(color = Color.White, fontSize = 30.sp))
    }
}

@Composable
fun VideoFramesList(
    media: MediaMetadataRetriever,
    frameSelectedCallback: (Int) -> Unit
) {
    val frameCount =
        media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_FRAME_COUNT) ?: "0"
    val listItemModifier = Modifier
        .padding(5.dp)
        .height(80.dp)
    val selectedListItemModifier = listItemModifier.border(4.dp, Color.White)
    val selectedIdx = remember { mutableStateOf(0) }
    frameSelectedCallback.invoke(selectedIdx.value)
    Column {
        Text("Total Frames: $frameCount", modifier = Modifier.padding(10.dp))
        Column(
            modifier = Modifier
                .border(2.dp, Color.Gray)
                .background(Color.LightGray)
                .fillMaxWidth()
        ) {
            LazyRow(modifier = Modifier.padding(10.dp)) {
                items(
                    count = frameCount.toInt(),
                    itemContent = { idx ->
                        media.getFrameAtIndex(idx)?.asImageBitmap()?.let {
                            Image(
                                it,
                                "Frame number $idx",
                                (if (selectedIdx.value == idx) selectedListItemModifier else listItemModifier).clickable {
                                    frameSelectedCallback.invoke(idx)
                                    selectedIdx.value = idx
                                },
                                Alignment.Center,
                                ContentScale.Fit,
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
//    VideoFramesList(media){}
}