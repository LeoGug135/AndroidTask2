package com.example.task2compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. 定义任务数据模型
data class Task(
    val id: Int,
    val title: String,
    val isDone: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CourseTaskScreen()
            }
        }
    }
}

@Composable
fun CourseTaskScreen() {
    // 2. 定义状态：初始有3个任务，第1个已完成
    val tasks = remember {
        mutableStateListOf(
            Task(1, "学习 Column 和 Row", true),
            Task(2, "学习状态管理", false),
            Task(3, "完成 Compose 实验", false)
        )
    }
    // 输入框状态
    var inputText by remember { mutableStateOf("") }

    // 动态计算已完成数量和总数量
    val doneCount = tasks.count { it.isDone }
    val totalCount = tasks.size

    // 3. 整体布局
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // 标题
        Text(
            text = "课程学习任务",
            color = Color(0xFFD32F2F),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 输入框 + 添加按钮（横向排列）
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("请输入学习任务", fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    // 添加任务逻辑
                    if (inputText.isNotBlank()) {
                        val newId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
                        tasks.add(Task(newId, inputText.trim()))
                        inputText = "" // 清空输入框
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("添加", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 已完成计数
        Text(
            text = "已完成：$doneCount / $totalCount",
            color = Color.Gray,
            fontSize = 14.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 任务列表（LazyColumn）
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(tasks) { index, task ->
                TaskItem(
                    task = task,
                    onCheckedChange = { checked ->
                        // 更新状态：复制出新对象替换旧的
                        tasks[index] = task.copy(isDone = checked)
                    },
                    onDelete = {
                        // 删除任务
                        tasks.removeAt(index)
                    }
                )
            }
        }
    }
}

// 4. 单独抽取的单个任务卡片的 UI 组件
@Composable
fun TaskItem(
    task: Task,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)) // 浅灰色卡片
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 复选框
            Checkbox(
                checked = task.isDone,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFFD32F2F))
            )
            // 任务标题（根据完成状态动态渲染删除线和颜色）
            Text(
                text = task.title,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp),
                fontSize = 16.sp,
                textDecoration = if (task.isDone) TextDecoration.LineThrough else TextDecoration.None,
                color = if (task.isDone) Color.Gray else Color.Black
            )
            // 删除按钮
            TextButton(onClick = onDelete) {
                Text("删除", color = Color(0xFFD32F2F))
            }
        }
    }
}