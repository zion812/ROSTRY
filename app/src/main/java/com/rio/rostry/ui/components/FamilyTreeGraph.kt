package com.rio.rostry.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rio.rostry.domain.model.FamilyTreeNode

@Composable
fun FamilyTreeGraph(
    familyTree: FamilyTreeNode?,
    modifier: Modifier = Modifier
) {
    if (familyTree == null) return
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Root node
        FamilyTreeNodeItem(
            node = familyTree,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        // Children nodes
        if (familyTree.children.isNotEmpty()) {
            // Connection lines
            Spacer(modifier = Modifier.height(20.dp))
            
            // Children row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                familyTree.children.forEach { child ->
                    FamilyTreeNodeItem(node = child)
                }
            }
        }
    }
}

@Composable
fun FamilyTreeNodeItem(
    node: FamilyTreeNode,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Node visualization
        Canvas(
            modifier = Modifier
                .size(60.dp)
        ) {
            drawCircle(
                color = if (node.poultry.gender == "MALE") Color.Blue else Color.Red,
                radius = 25f,
                style = Stroke(width = 2f)
            )
        }
        
        // Node information
        Text(
            text = node.poultry.name,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 4.dp)
                .width(80.dp)
        )
        
        Text(
            buildAnnotatedString { append("Gen ${node.poultry.generation}") },
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 2.dp)
                .width(80.dp)
        )
    }
}