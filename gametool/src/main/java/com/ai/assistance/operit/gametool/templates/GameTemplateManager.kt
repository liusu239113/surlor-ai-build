package com.ai.assistance.operit.gametool.templates

import com.ai.assistance.operit.gametool.models.GameProject
import com.ai.assistance.operit.gametool.models.GameType
import com.ai.assistance.operit.gametool.models.GameSettings
import com.ai.assistance.operit.gametool.models.Resolution
import com.ai.assistance.operit.gametool.models.Orientation
import com.ai.assistance.operit.gametool.models.SourceFile

/**
 * 游戏模板管理器
 * 提供预置的游戏项目模板，用户可以从模板快速开始
 */
object GameTemplateManager {

    data class GameTemplate(
        val id: String,
        val name: String,
        val description: String,
        val type: GameType,
        val icon: String = "🎮",
        val category: TemplateCategory = TemplateCategory.BASIC
    )

    enum class TemplateCategory {
        BASIC,      // 基础模板
        ACTION,     // 动作游戏
        PUZZLE,     // 益智游戏
        PLATFORM,   // 平台跳跃
        SHOOTER,    // 射击游戏
        RACING,     // 竞速游戏
        CARD,       // 卡牌游戏
        EMPTY       // 空白项目
    }

    /**
     * 获取所有可用模板
     */
    fun getTemplates(): List<GameTemplate> = listOf(
        GameTemplate("empty_h5", "空白 H5 项目", "从零开始的 H5 游戏", GameType.H5, "📄", TemplateCategory.EMPTY),
        GameTemplate("empty_compose", "空白 Compose 项目", "从零开始的 Compose 原生游戏", GameType.COMPOSE, "📄", TemplateCategory.EMPTY),
        GameTemplate("dino_runner", "快速奔跑", "类似 Chrome 小恐龙的跑酷游戏", GameType.H5, "🦖", TemplateCategory.ACTION),
        GameTemplate("flappy_bird", "飞翔小鸟", "经典的 Flappy Bird 风格", GameType.H5, "🐦", TemplateCategory.ACTION),
        GameTemplate("platformer", "平台跳跃", "2D 平台跳跃游戏", GameType.H5, "🏃", TemplateCategory.PLATFORM),
        GameTemplate("puzzle_match", "消除游戏", "三消/连连看风格", GameType.H5, "🧩", TemplateCategory.PUZZLE),
        GameTemplate("shooter", "射击小游戏", "纵版射击游戏", GameType.H5, "🎯", TemplateCategory.SHOOTER),
        GameTemplate("snake", "贪吃蛇", "经典贪吃蛇游戏", GameType.H5, "🐍", TemplateCategory.BASIC),
        GameTemplate("tetris", "俄罗斯方块", "经典俄罗斯方块", GameType.H5, "🧱", TemplateCategory.PUZZLE),
        GameTemplate("clicker", "点击放置", "点击放置类增量游戏", GameType.H5, "👆", TemplateCategory.BASIC),
        GameTemplate("pong", "乒乓球", "经典双人乒乓球", GameType.H5, "🏓", TemplateCategory.BASIC),
        GameTemplate("space_shooter", "太空射击", "太空飞船射击游戏", GameType.H5, "🚀", TemplateCategory.SHOOTER),
    )

    /**
     * 根据模板创建游戏项目
     */
    fun createProjectFromTemplate(templateId: String): GameProject? {
        val template = getTemplates().find { it.id == templateId } ?: return null
        val gameCode = getTemplateCode(templateId) ?: return null

        return GameProject(
            name = template.name,
            type = template.type,
            description = template.description,
            templateId = templateId,
            entryFile = "index.html",
            sourceFiles = listOf(gameCode),
            settings = getDefaultSettings(templateId)
        )
    }

    /**
     * 创建空白项目
     */
    fun createEmptyProject(type: GameType): GameProject {
        val entryCode = when (type) {
            GameType.H5 -> getEmptyH5Template()
            GameType.COMPOSE -> getEmptyComposeTemplate()
            GameType.GODOT -> getEmptyGodotTemplate()
        }
        val entryFile = when (type) {
            GameType.H5 -> "index.html"
            GameType.COMPOSE -> "MainGame.kt"
            GameType.GODOT -> "main.gd"
        }

        return GameProject(
            name = "新游戏项目",
            type = type,
            templateId = "empty_${type.name.lowercase()}",
            entryFile = entryFile,
            sourceFiles = listOf(SourceFile(path = entryFile, content = entryCode)),
            settings = GameSettings()
        )
    }

    private fun getTemplateCode(templateId: String): SourceFile? {
        val code = when (templateId) {
            "empty_h5" -> getEmptyH5Template()
            "dino_runner" -> getDinoRunnerTemplate()
            "flappy_bird" -> getFlappyBirdTemplate()
            "snake" -> getSnakeTemplate()
            "tetris" -> getTetrisTemplate()
            "clicker" -> getClickerTemplate()
            "pong" -> getPongTemplate()
            "space_shooter" -> getSpaceShooterTemplate()
            "platformer" -> getPlatformerTemplate()
            "puzzle_match" -> getPuzzleMatchTemplate()
            "shooter" -> getShooterTemplate()
            else -> null
        } ?: return null

        return SourceFile(path = "index.html", content = code)
    }

    private fun getDefaultSettings(templateId: String): GameSettings {
        return when (templateId) {
            "flappy_bird", "space_shooter", "shooter" -> GameSettings(
                orientation = Orientation.PORTRAIT
            )
            "dino_runner", "platformer", "pong" -> GameSettings(
                orientation = Orientation.LANDSCAPE
            )
            else -> GameSettings()
        }
    }

    // ===================== 模板代码 =====================

    private fun getEmptyH5Template(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
* { margin:0; padding:0; box-sizing:border-box; }
body { width:100vw; height:100vh; overflow:hidden; background:#1a1a2e; display:flex; justify-content:center; align-items:center; touch-action:none; }
canvas { display:block; }
</style>
</head>
<body>
<canvas id="game"></canvas>
<script>
(function(){
    'use strict';
    const c=document.getElementById('game'),ctx=c.getContext('2d');
    function r(){c.width=innerWidth;c.height=innerHeight;} r();
    addEventListener('resize',r);
    const s={started:false,over:false,score:0};
    function loop(t){u(t);draw();requestAnimationFrame(loop);}
    function u(t){}
    function draw(){
        ctx.fillStyle='#1a1a2e';ctx.fillRect(0,0,c.width,c.height);
        ctx.fillStyle='#e94560';ctx.font='24px sans-serif';ctx.textAlign='center';
        ctx.fillText('✨ 在这里开始你的游戏 ✨',c.width/2,c.height/2);
        ctx.fillStyle='#0f3460';ctx.fillRect(c.width/2-80,c.height/2+30,160,50);
        ctx.fillStyle='#fff';ctx.font='18px sans-serif';
        ctx.fillText('点击开始',c.width/2,c.height/2+64);
    }
    addEventListener('click',()=>{if(!s.started)s.started=true;});
    addEventListener('touchstart',e=>{e.preventDefault();if(!s.started)s.started=true;});
    requestAnimationFrame(loop);
})();
</script>
</body>
</html>
    """.trimIndent()

    private fun getEmptyComposeTemplate(): String = """
package com.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { GameScreen() }
    }
}

@Composable
fun GameScreen() {
    var score by remember { mutableStateOf(0) }
    var started by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(16) // ~60 FPS
            if (started) { /* 游戏更新逻辑 */ }
        }
    }
    
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E))
            .pointerInput(Unit) {
                detectTapGestures {
                    if (!started) started = true
                    else score++
                }
            }
    ) {
        // 游戏渲染逻辑
        drawCircle(Color(0xFFE94560), 50f, center)
    }
}
    """.trimIndent()

    private fun getEmptyGodotTemplate(): String = """
extends Node2D

var score = 0
var game_started = false

func _ready():
    get_node("StartButton").show()
    get_node("GameOver").hide()

func _process(delta):
    if not game_started:
        return
    # 游戏逻辑

func start_game():
    game_started = true
    score = 0
    get_node("StartButton").hide()
    get_node("GameOver").hide()

func game_over():
    game_started = false
    get_node("GameOver").show()

func _on_StartButton_pressed():
    start_game()
    """.trimIndent()

    private fun getDinoRunnerTemplate(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
body{width:100vw;height:100vh;overflow:hidden;background:#f7f7f7;display:flex;justify-content:center;align-items:center;font-family:sans-serif;touch-action:none;-webkit-user-select:none;user-select:none;}
canvas{display:block;background:#f7f7f7;}
</style>
</head>
<body>
<canvas id="game"></canvas>
<script>
(function(){
    'use strict';
    const c=document.getElementById('game'),ctx=c.getContext('2d');
    function resize(){c.width=innerWidth;c.height=innerHeight;} resize();
    addEventListener('resize',resize);
    
    const GROUND_Y=()=>c.height-60;
    const GRAVITY=0.6;
    const JUMP=-12;
    
    let s={score:0,highScore:0,started:false,over:false,speed:6};
    let dino={x:50,y:GROUND_Y(),vy:0,w:40,h:44,jumping:false};
    let obstacles=[];
    let frame=0;
    
    function reset(){
        s={score:0,highScore:s.highScore,started:false,over:false,speed:6};
        dino={x:50,y:GROUND_Y(),vy:0,w:40,h:44,jumping:false};
        obstacles=[];
        frame=0;
    }
    
    function loop(){
        u();draw();requestAnimationFrame(loop);
    }
    
    function u(){
        frame++;
        if(!s.started||s.over)return;
        
        // Dino physics
        dino.vy+=GRAVITY;
        dino.y+=dino.vy;
        if(dino.y>=GROUND_Y()){dino.y=GROUND_Y();dino.vy=0;dino.jumping=false;}
        
        // Speed up
        s.speed=6+Math.floor(s.score/100)*0.5;
        
        // Spawn obstacles
        if(frame%Math.max(60,120-s.speed*5)==0){
            let oh=25+Math.random()*30;
            obstacles.push({x:c.width,y:GROUND_Y()-oh,w:20,h:oh});
        }
        
        // Move obstacles
        obstacles=obstacles.filter(o=>{
            o.x-=s.speed;
            if(o.x+o.w<0)return false;
            // Collision
            if(dino.x+dino.w>o.x&&dino.x<o.x+o.w&&dino.y+dino.h>o.y){
                s.over=true;
            }
            if(o.x+o.w<dino.x&&!o.scored){o.scored=true;s.score++;}
            return true;
        });
    }
    
    function draw(){
        ctx.clearRect(0,0,c.width,c.height);
        
        // Ground
        ctx.fillStyle='#535353';
        ctx.fillRect(0,GROUND_Y()+44,c.width,2);
        
        if(!s.started){
            ctx.fillStyle='#535353';
            ctx.font='24px sans-serif';
            ctx.textAlign='center';
            ctx.fillText('🦖 小恐龙快跑',c.width/2,c.height/2-60);
            ctx.fillStyle='#e94560';
            ctx.fillRect(c.width/2-90,c.height/2-20,180,50);
            ctx.fillStyle='#fff';
            ctx.font='18px sans-serif';
            ctx.fillText('点击 / 触屏开始',c.width/2,c.height/2+12);
            return;
        }
        
        // Score
        ctx.fillStyle='#535353';
        ctx.font='18px sans-serif';
        ctx.textAlign='right';
        ctx.fillText('分数: '+s.score,c.width-16,32);
        
        // Dino
        ctx.fillStyle='#535353';
        const legOffset=Math.sin(frame*0.2)*8;
        ctx.fillRect(dino.x,dino.y,dino.w,dino.h);
        // Eyes
        ctx.fillStyle='#fff';
        ctx.fillRect(dino.x+30,dino.y+8,8,8);
        ctx.fillStyle='#000';
        ctx.fillRect(dino.x+34,dino.y+10,4,4);
        
        // Obstacles
        ctx.fillStyle='#535353';
        obstacles.forEach(o=>ctx.fillRect(o.x,o.y,o.w,o.h));
        
        if(s.over){
            ctx.fillStyle='rgba(0,0,0,0.5)';
            ctx.fillRect(0,0,c.width,c.height);
            ctx.fillStyle='#e94560';
            ctx.font='bold 28px sans-serif';
            ctx.textAlign='center';
            ctx.fillText('游戏结束',c.width/2,c.height/2-40);
            ctx.fillStyle='#fff';
            ctx.font='18px sans-serif';
            ctx.fillText('得分: '+s.score,c.width/2,c.height/2);
            ctx.fillStyle='#0f3460';
            ctx.fillRect(c.width/2-70,c.height/2+30,140,45);
            ctx.fillStyle='#fff';
            ctx.font='16px sans-serif';
            ctx.fillText('重新开始',c.width/2,c.height/2+60);
        }
    }
    
    function jump(){
        if(s.over){reset();s.started=true;return;}
        if(!s.started){s.started=true;return;}
        if(!dino.jumping){dino.vy=JUMP;dino.jumping=true;}
    }
    
    addEventListener('click',jump);
    addEventListener('touchstart',e=>{e.preventDefault();jump();});
    addEventListener('keydown',e=>{if(e.code=='Space')jump();});
    
    requestAnimationFrame(loop);
})();
</script>
</body>
</html>
    """.trimIndent()

    private fun getFlappyBirdTemplate(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
body{width:100vw;height:100vh;overflow:hidden;background:#70c5ce;display:flex;justify-content:center;align-items:center;touch-action:none;}
canvas{display:block;}
</style>
</head>
<body>
<canvas id="game"></canvas>
<script>
(function(){
    'use strict';
    const c=document.getElementById('game'),ctx=c.getContext('2d');
    function r(){c.width=innerWidth;c.height=innerHeight;} r();
    addEventListener('resize',r);
    
    const GRAVITY=0.5,FLAP=-8,PIPE_W=50,GAP=150;
    let s={started:false,over:false,score:0};
    let bird={x:60,y:200,vy:0,r:15};
    let pipes=[];
    let frame=0;
    
    function reset(){
        s={started:false,over:false,score:0};
        bird={x:60,y:c.height/2,vy:0,r:15};
        pipes=[];
        frame=0;
    }
    
    function loop(t){u();draw();requestAnimationFrame(loop);}
    
    function u(){
        frame++;
        if(!s.started||s.over)return;
        
        bird.vy+=GRAVITY;
        bird.y+=bird.vy;
        
        if(bird.y+bird.r>c.height||bird.y-bird.r<0){
            s.over=true;
            return;
        }
        
        if(frame%90==0){
            let ph=80+Math.random()*(c.height-300);
            pipes.push({x:c.width,top:ph,bottom:ph+GAP,scored:false});
        }
        
        pipes=pipes.filter(p=>{
            p.x-=3;
            if(p.x+PIPE_W<0)return false;
            // Collision
            if(bird.x+bird.r>p.x&&bird.x-bird.r<p.x+PIPE_W){
                if(bird.y-bird.r<p.top||bird.y+bird.r>p.bottom){
                    s.over=true;
                }
            }
            if(p.x+PIPE_W<bird.x&&!p.scored){p.scored=true;s.score++;}
            return true;
        });
    }
    
    function draw(){
        ctx.fillStyle='#70c5ce';ctx.fillRect(0,0,c.width,c.height);
        
        if(!s.started){
            ctx.fillStyle='#fff';ctx.font='bold 30px sans-serif';ctx.textAlign='center';
            ctx.fillText('🐦 Flappy Bird',c.width/2,c.height/3);
            ctx.fillStyle='#e94560';
            ctx.fillRect(c.width/2-90,c.height/2-20,180,50);
            ctx.fillStyle='#fff';ctx.font='18px sans-serif';
            ctx.fillText('点击开始',c.width/2,c.height/2+12);
            return;
        }
        
        // Pipes
        pipes.forEach(p=>{
            ctx.fillStyle='#73b365';
            ctx.fillRect(p.x,0,PIPE_W,p.top);
            ctx.fillRect(p.x,p.bottom,PIPE_W,c.height-p.bottom);
            ctx.fillStyle='#4a8f3f';
            ctx.fillRect(p.x-3,p.top-20,PIPE_W+6,20);
            ctx.fillRect(p.x-3,p.bottom,PIPE_W+6,20);
        });
        
        // Bird
        ctx.fillStyle='#f5c842';
        ctx.beginPath();
        ctx.arc(bird.x,bird.y,bird.r,0,Math.PI*2);
        ctx.fill();
        ctx.fillStyle='#fff';ctx.beginPath();
        ctx.arc(bird.x+6,bird.y-4,4,0,Math.PI*2);ctx.fill();
        ctx.fillStyle='#000';ctx.beginPath();
        ctx.arc(bird.x+8,bird.y-4,2,0,Math.PI*2);ctx.fill();
        
        // Score
        ctx.fillStyle='#fff';ctx.font='bold 24px sans-serif';ctx.textAlign='center';
        ctx.fillText(s.score,c.width/2,40);
        
        if(s.over){
            ctx.fillStyle='rgba(0,0,0,0.4)';ctx.fillRect(0,0,c.width,c.height);
            ctx.fillStyle='#fff';ctx.font='bold 30px sans-serif';
            ctx.fillText('游戏结束',c.width/2,c.height/2-30);
            ctx.font='20px sans-serif';
            ctx.fillText('得分: '+s.score,c.width/2,c.height/2+10);
            ctx.fillStyle='#e94560';
            ctx.fillRect(c.width/2-70,c.height/2+40,140,45);
            ctx.fillStyle='#fff';ctx.font='16px sans-serif';
            ctx.fillText('重新开始',c.width/2,c.height/2+70);
        }
    }
    
    function flap(){
        if(s.over){reset();s.started=true;return;}
        if(!s.started){s.started=true;}
        bird.vy=FLAP;
    }
    
    addEventListener('click',flap);
    addEventListener('touchstart',e=>{e.preventDefault();flap();});
    addEventListener('keydown',e=>{if(e.code=='Space')flap();});
    
    requestAnimationFrame(loop);
})();
</script>
</body>
</html>
    """.trimIndent()

    private fun getSnakeTemplate(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
body{width:100vw;height:100vh;overflow:hidden;background:#1a1a2e;display:flex;justify-content:center;align-items:center;touch-action:none;}
canvas{display:block;}
</style>
</head>
<body>
<canvas id="game"></canvas>
<script>
(function(){
    'use strict';
    const c=document.getElementById('game'),ctx=c.getContext('2d');
    const S=20;let cols,rows;
    function resize(){c.width=innerWidth;c.height=innerHeight;cols=Math.floor(c.width/S);rows=Math.floor(c.height/S);}
    resize();addEventListener('resize',resize);
    let s={started:false,over:false,score:0};
    let snake=[{x:5,y:5}],food={x:10,y:10},dir={x:1,y:0},nextDir={x:1,y:0},tick=0;
    function spawnFood(){food={x:Math.floor(Math.random()*cols),y:Math.floor(Math.random()*rows)};}
    function reset(){snake=[{x:5,y:5}];dir={x:1,y:0};nextDir={x:1,y:0};tick=0;s={started:false,over:false,score:0};spawnFood();}
    spawnFood();
    function loop(){u();draw();requestAnimationFrame(loop);}
    function u(){
        if(!s.started||s.over)return;
        tick++;if(tick%8!=0)return;
        dir=nextDir;
        let head={x:snake[0].x+dir.x,y:snake[0].y+dir.y};
        if(head.x<0||head.x>=cols||head.y<0||head.y>=rows){s.over=true;return;}
        if(snake.some(s=>s.x==head.x&&s.y==head.y)){s.over=true;return;}
        snake.unshift(head);
        if(head.x==food.x&&head.y==food.y){s.score++;spawnFood();}
        else snake.pop();
    }
    function draw(){
        ctx.fillStyle='#1a1a2e';ctx.fillRect(0,0,c.width,c.height);
        if(!s.started){
            ctx.fillStyle='#e94560';ctx.font='bold 28px sans-serif';ctx.textAlign='center';
            ctx.fillText('🐍 贪吃蛇',c.width/2,c.height/3);
            ctx.fillRect(c.width/2-80,c.height/2-15,160,50);
            ctx.fillStyle='#fff';ctx.font='18px sans-serif';
            ctx.fillText('点击开始',c.width/2,c.height/2+12);
            return;
        }
        // Food
        ctx.fillStyle='#e94560';
        ctx.fillRect(food.x*S,food.y*S,S,S);
        // Snake
        snake.forEach((p,i)=>{
            ctx.fillStyle=i==0?'#0f3460':'#16213e';
            ctx.fillRect(p.x*S+1,p.y*S+1,S-2,S-2);
        });
        ctx.fillStyle='#fff';ctx.font='20px sans-serif';ctx.textAlign='left';
        ctx.fillText('分数: '+s.score,10,30);
        if(s.over){
            ctx.fillStyle='rgba(0,0,0,0.5)';ctx.fillRect(0,0,c.width,c.height);
            ctx.fillStyle='#e94560';ctx.font='bold 28px sans-serif';ctx.textAlign='center';
            ctx.fillText('游戏结束',c.width/2,c.height/2-30);
            ctx.fillStyle='#fff';ctx.font='18px sans-serif';
            ctx.fillText('得分: '+s.score,c.width/2,c.height/2+10);
            ctx.fillRect(c.width/2-70,c.height/2+40,140,45);
            ctx.fillStyle='#fff';ctx.font='16px sans-serif';
            ctx.fillText('重新开始',c.width/2,c.height/2+70);
        }
    }
    document.addEventListener('keydown',e=>{
        if(e.code=='Space'&&s.over){reset();s.started=true;return;}
        if(!s.started){s.started=true;return;}
        switch(e.code){
            case'ArrowUp':if(dir.y!=1)nextDir={x:0,y:-1};break;
            case'ArrowDown':if(dir.y!=-1)nextDir={x:0,y:1};break;
            case'ArrowLeft':if(dir.x!=1)nextDir={x:-1,y:0};break;
            case'ArrowRight':if(dir.x!=-1)nextDir={x:1,y:0};break;
        }
    });
    addEventListener('click',()=>{if(!s.started){s.started=true;return;}if(s.over){reset();s.started=true;}});
    requestAnimationFrame(loop);
})();
</script>
</body>
</html>
    """.trimIndent()

    private fun getTetrisTemplate(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
body{width:100vw;height:100vh;overflow:hidden;background:#1a1a2e;display:flex;justify-content:center;align-items:center;touch-action:none;color:#fff;font-family:sans-serif;}
canvas{display:block;border:2px solid #0f3460;}
</style>
</head>
<body>
<canvas id="game"></canvas>
<script>
(function(){
    'use strict';
    const c=document.getElementById('game'),ctx=c.getContext('2d');
    const COLS=10,ROWS=20,S=Math.min(Math.floor((innerWidth-20)/COLS),Math.floor((innerHeight-60)/ROWS));
    c.width=COLS*S;c.height=ROWS*S;
    
    const PIECES=[[[1,1,1,1]],[[1,1],[1,1]],[[0,1,0],[1,1,1]],[[1,0],[1,1],[0,1]],[[0,1],[1,1],[1,0]],[[1,1,0],[0,1,1]],[[0,1,1],[1,1,0]]];
    const COLORS=['#e94560','#0f3460','#533483','#e94560','#16213e','#e94560','#533483'];
    
    let s={started:false,over:false,score:0};
    let board=Array.from({length:ROWS},()=>Array(COLS).fill(0));
    let piece={shape:null,x:0,y:0,color:''},nextPiece=null,tick=0;
    
    function newPiece(){return{shape:PIECES[Math.floor(Math.random()*PIECES.length)],x:Math.floor((COLS-1)/2),y:0,color:COLORS[Math.floor(Math.random()*COLORS.length)]};}
    function reset(){board=Array.from({length:ROWS},()=>Array(COLS).fill(0));piece=newPiece();nextPiece=newPiece();tick=0;s={started:false,over:false,score:0};}
    
    function valid(p,ox,oy){return p.shape.every((r,dy)=>r.every((v,dx)=>!v||(ox+dx>=0&&ox+dx<COLS&&oy+dy<ROWS&&!board[oy+dy][ox+dx])));}
    function lock(){piece.shape.forEach((r,dy)=>r.forEach((v,dx)=>{if(v)board[piece.y+dy][piece.x+dx]=piece.color;}));
        let cleared=0;for(let y=ROWS-1;y>=0;y--){if(board[y].every(v=>v!==0)){board.splice(y,1);board.unshift(Array(COLS).fill(0));y++;cleared++;}}
        if(cleared)s.score+=cleared*100;piece=nextPiece;nextPiece=newPiece();
        if(!valid(piece,piece.x,piece.y))s.over=true;
    }
    
    function loop(){u();draw();requestAnimationFrame(loop);}
    function u(){
        if(!s.started||s.over)return;
        tick++;if(tick%30!=0)return;
        if(valid(piece,piece.x,piece.y+1))piece.y++;else lock();
    }
    
    function draw(){
        ctx.fillStyle='#1a1a2e';ctx.fillRect(0,0,c.width,c.height);
        board.forEach((r,y)=>r.forEach((v,x)=>{if(v){ctx.fillStyle=v;ctx.fillRect(x*S,y*S,S-1,S-1);}}));
        if(piece&&!s.over){ctx.fillStyle=piece.color;
            piece.shape.forEach((r,dy)=>r.forEach((v,dx)=>{if(v)ctx.fillRect((piece.x+dx)*S,(piece.y+dy)*S,S-1,S-1);}));}
        if(!s.started&&!s.over){
            ctx.fillStyle='#e94560';ctx.font='bold 24px sans-serif';ctx.textAlign='center';
            ctx.fillText('🧱 俄罗斯方块',c.width/2,c.height/3);
            ctx.fillRect(c.width/2-70,c.height/2-15,140,50);
            ctx.fillStyle='#fff';ctx.font='18px sans-serif';
            ctx.fillText('点击开始',c.width/2,c.height/2+12);}
        if(s.over){
            ctx.fillStyle='rgba(0,0,0,0.5)';ctx.fillRect(0,0,c.width,c.height);
            ctx.fillStyle='#fff';ctx.font='bold 24px sans-serif';ctx.textAlign='center';
            ctx.fillText('游戏结束',c.width/2,c.height/2-30);
            ctx.font='18px sans-serif';ctx.fillText('得分: '+s.score,c.width/2,c.height/2+5);
            ctx.fillStyle='#e94560';ctx.fillRect(c.width/2-60,c.height/2+30,120,40);
            ctx.fillStyle='#fff';ctx.font='14px sans-serif';
            ctx.fillText('重新开始',c.width/2,c.height/2+55);}
        ctx.fillStyle='#fff';ctx.font='16px sans-serif';ctx.textAlign='left';
        ctx.fillText('分数: '+s.score,8,24);
    }
    
    document.addEventListener('keydown',e=>{
        if(!s.started){s.started=true;return;}
        if(s.over){if(e.code=='Space'){reset();s.started=true;}return;}
        switch(e.code){
            case'ArrowLeft':if(valid(piece,piece.x-1,piece.y))piece.x--;break;
            case'ArrowRight':if(valid(piece,piece.x+1,piece.y))piece.x++;break;
            case'ArrowDown':if(valid(piece,piece.x,piece.y+1))piece.y++;break;
            case'ArrowUp':{let r=piece.shape[0].map((_,i)=>piece.shape.map(r=>r[i]).reverse());let p2={...piece,shape:r};if(valid(p2,piece.x,piece.y))piece.shape=r;}break;
            case'Space':while(valid(piece,piece.x,piece.y+1))piece.y++;lock();break;
        }
    });
    addEventListener('click',()=>{if(!s.started){s.started=true;return;}if(s.over){reset();s.started=true;}});
    reset();
    requestAnimationFrame(loop);
})();
</script>
</body>
</html>
    """.trimIndent()

    private fun getClickerTemplate(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
body{width:100vw;height:100vh;overflow:hidden;background:#1a1a2e;display:flex;justify-content:center;align-items:center;flex-direction:column;color:#fff;font-family:sans-serif;text-align:center;touch-action:none;}
</style>
</head>
<body>
<div id="app">
    <h1 style="font-size:28px;margin:20px 0;color:#e94560;">👆 点击放置</h1>
    <div style="font-size:48px;margin:20px 0;" id="scoreDisplay">0</div>
    <p style="color:#aaa;font-size:14px;">金币</p>
    <button id="clickBtn" style="width:120px;height:120px;border-radius:60px;border:none;background:#e94560;color:#fff;font-size:36px;margin:30px 0;cursor:pointer;box-shadow:0 8px 24px rgba(233,69,96,0.4);">💰</button>
    <div style="display:flex;gap:10px;flex-wrap:wrap;justify-content:center;max-width:300px;">
        <button class="upgrade" data-cost="10" data-mult="1">点击 +1<br><small>10 金币</small></button>
        <button class="upgrade" data-cost="50" data-mult="2">点击 +2<br><small>50 金币</small></button>
        <button class="upgrade" data-cost="200" data-mult="5">点击 +5<br><small>200 金币</small></button>
    </div>
    <div style="margin-top:10px;font-size:12px;color:#666;">每秒自动: +<span id="autoRate">0</span></div>
</div>
<script>
let score=0,clickPower=1,autoRate=0,started=true;
const scoreEl=document.getElementById('scoreDisplay'),autoEl=document.getElementById('autoRate');
document.getElementById('clickBtn').addEventListener('click',()=>{score+=clickPower;update();});
document.querySelectorAll('.upgrade').forEach(b=>b.addEventListener('click',function(){
    let cost=parseInt(this.dataset.cost),mult=parseInt(this.dataset.mult);
    if(score>=cost){score-=cost;clickPower+=mult;this.style.opacity='0.5';this.disabled=true;update();}
}));
setInterval(()=>{if(started){score+=autoRate;update();}},1000);
function update(){scoreEl.textContent=score.toLocaleString();autoEl.textContent=autoRate;}
update();
</script>
</body>
</html>
    """.trimIndent()

    private fun getPongTemplate(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
body{width:100vw;height:100vh;overflow:hidden;background:#1a1a2e;display:flex;justify-content:center;align-items:center;color:#fff;font-family:sans-serif;touch-action:none;}
canvas{display:block;background:#16213e;border-radius:8px;}
</style>
</head>
<body>
<canvas id="game"></canvas>
<script>
(function(){
    'use strict';
    const c=document.getElementById('game'),ctx=c.getContext('2d');
    const W=Math.min(innerWidth-16,600),H=Math.min(innerHeight-16,400);
    c.width=W;c.height=H;
    const PW=8,BALL=6,SPEED=4;
    let s={started:false,over:false,score:[0,0]};
    let p1={x:10,y:H/2-30,w:PW,h:60,vy:0};
    let p2={x:W-10-PW,y:H/2-30,w:PW,h:60,vy:0};
    let ball={x:W/2,y:H/2,vx:SPEED,vy:SPEED};
    function reset(){ball={x:W/2,y:H/2,vx:SPEED*(Math.random()>0.5?1:-1),vy:SPEED*(Math.random()>0.5?1:-1)};s.started=true;s.over=false;}
    function loop(){u();draw();requestAnimationFrame(loop);}
    function u(){
        if(!s.started||s.over)return;
        p1.y+=p1.vy;p2.y+=p2.vy;
        p1.y=Math.max(0,Math.min(H-p1.h,p1.y));
        p2.y=Math.max(0,Math.min(H-p2.h,p2.y));
        ball.x+=ball.vx;ball.y+=ball.vy;
        if(ball.y-BALL<0||ball.y+BALL>H)ball.vy*=-1;
        if(ball.x-BALL<p1.x+PW&&ball.x+BALL>p1.x&&ball.y>p1.y&&ball.y<p1.y+p1.h){ball.vx*=-1;ball.x=p1.x+PW+BALL;}
        if(ball.x+BALL>p2.x&&ball.x-BALL<p2.x+PW&&ball.y>p2.y&&ball.y<p2.y+p2.h){ball.vx*=-1;ball.x=p2.x-BALL;}
        if(ball.x<0){s.score[1]++;reset();if(s.score[1]>=5)s.over=true;}
        if(ball.x>W){s.score[0]++;reset();if(s.score[0]>=5)s.over=true;}
    }
    function draw(){
        ctx.fillStyle='#16213e';ctx.fillRect(0,0,W,H);
        ctx.fillStyle='#e94560';ctx.fillRect(p1.x,p1.y,p1.w,p1.h);
        ctx.fillStyle='#0f3460';ctx.fillRect(p2.x,p2.y,p2.w,p2.h);
        ctx.fillStyle='#fff';ctx.beginPath();ctx.arc(ball.x,ball.y,BALL,0,Math.PI*2);ctx.fill();
        ctx.setLineDash([5,10]);ctx.strokeStyle='#533483';ctx.beginPath();ctx.moveTo(W/2,0);ctx.lineTo(W/2,H);ctx.stroke();
        ctx.fillStyle='#fff';ctx.font='20px sans-serif';ctx.textAlign='center';
        ctx.fillText(s.score[0]+' : '+s.score[1],W/2,28);
        if(!s.started){
            ctx.fillStyle='rgba(0,0,0,0.5)';ctx.fillRect(0,0,W,H);
            ctx.fillStyle='#e94560';ctx.font='bold 28px sans-serif';ctx.fillText('🏓 乒乓球',W/2,H/3);
            ctx.fillRect(W/2-80,H/2-15,160,50);
            ctx.fillStyle='#fff';ctx.font='18px sans-serif';ctx.fillText('点击开始',W/2,H/2+12);
        }
        if(s.over){
            ctx.fillStyle='rgba(0,0,0,0.6)';ctx.fillRect(0,0,W,H);
            ctx.fillStyle='#fff';ctx.font='bold 24px sans-serif';ctx.fillText('🏆 '+(s.score[0]>s.score[1]?'左方':'右方')+'获胜！',W/2,H/2-15);
            ctx.fillStyle='#e94560';ctx.fillRect(W/2-80,H/2+15,160,45);
            ctx.fillStyle='#fff';ctx.font='16px sans-serif';ctx.fillText('再来一局',W/2,H/2+45);
        }
    }
    document.addEventListener('keydown',e=>{
        if(e.code=='KeyW')p1.vy=-5;if(e.code=='KeyS')p1.vy=5;
        if(e.code=='ArrowUp')p2.vy=-5;if(e.code=='ArrowDown')p2.vy=5;
    });
    document.addEventListener('keyup',e=>{
        if(e.code=='KeyW'||e.code=='KeyS')p1.vy=0;
        if(e.code=='ArrowUp'||e.code=='ArrowDown')p2.vy=0;
    });
    addEventListener('click',()=>{if(!s.started||s.over){s={started:true,over:false,score:[0,0]};reset();}});
    // AI for bottom player
    setInterval(()=>{if(s.started&&!s.over){p2.y+=(ball.y-(p2.y+p2.h/2))*0.1;}},33);
    requestAnimationFrame(loop);
})();
</script>
</body>
</html>
    """.trimIndent()

    private fun getSpaceShooterTemplate(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
body{width:100vw;height:100vh;overflow:hidden;background:#0a0a1a;display:flex;justify-content:center;align-items:center;touch-action:none;}
canvas{display:block;}
</style>
</head>
<body>
<canvas id="game"></canvas>
<script>
(function(){
    'use strict';
    const c=document.getElementById('game'),ctx=c.getContext('2d');
    function r(){c.width=innerWidth;c.height=innerHeight;}r();addEventListener('resize',r);
    let s={started:false,over:false,score:0};
    let player={x:c.width/2,y:c.height-80,w:40,h:40};
    let bullets=[],enemies=[],stars=[],frame=0;
    for(let i=0;i<50;i++)stars.push({x:Math.random()*c.width,y:Math.random()*c.height,s:Math.random()*3+1});
    function reset(){s={started:false,over:false,score:0};player={x:c.width/2,y:c.height-80,w:40,h:40};bullets=[];enemies=[];frame=0;}
    function loop(){u();draw();requestAnimationFrame(loop);}
    function u(){
        if(!s.started||s.over)return;frame++;
        if(frame%15==0)bullets.push({x:player.x+18,y:player.y,w:4,h:10});
        if(frame%40==0){let ew=20+Math.random()*20;enemies.push({x:Math.random()*(c.width-30),y:-30,w:ew,h:ew,s:1+Math.random()*2,hp:1});}
        bullets=bullets.filter(b=>{b.y-=7;return b.y>0;});
        enemies=enemies.filter(e=>{e.y+=e.s;
            bullets=bullets.filter(b=>{if(b.x<e.x+e.w&&b.x+b.w>e.x&&b.y<e.y+e.h&&b.y+b.h>e.y){e.hp--;return false;}return true;});
            if(e.hp<=0){s.score+=10;return false;}
            if(e.x<player.x+player.w&&e.x+e.w>player.x&&e.y<player.y+player.h&&e.y+e.h>player.y){s.over=true;}
            return e.y<c.height;
        });
    }
    function draw(){
        ctx.fillStyle='#0a0a1a';ctx.fillRect(0,0,c.width,c.height);
        stars.forEach(st=>{ctx.fillStyle='#fff';ctx.globalAlpha=st.s/4;ctx.fillRect(st.x,st.y,2,2);});ctx.globalAlpha=1;
        if(!s.started){
            ctx.fillStyle='#e94560';ctx.font='bold 28px sans-serif';ctx.textAlign='center';
            ctx.fillText('🚀 太空射击',c.width/2,c.height/3);
            ctx.fillRect(c.width/2-90,c.height/2-20,180,50);
            ctx.fillStyle='#fff';ctx.font='18px sans-serif';ctx.fillText('点击开始',c.width/2,c.height/2+12);
            player.y=c.height-80;return;
        }
        // Player
        ctx.fillStyle='#0f3460';ctx.fillRect(player.x,player.y,player.w,player.h);
        ctx.fillStyle='#e94560';ctx.fillRect(player.x+12,player.y-10,16,10);
        // Bullets
        ctx.fillStyle='#ff0';bullets.forEach(b=>ctx.fillRect(b.x,b.y,b.w,b.h));
        // Enemies
        ctx.fillStyle='#e94560';enemies.forEach(e=>ctx.fillRect(e.x,e.y,e.w,e.h));
        // Score
        ctx.fillStyle='#fff';ctx.font='18px sans-serif';ctx.textAlign='left';ctx.fillText('分数: '+s.score,10,28);
        if(s.over){
            ctx.fillStyle='rgba(0,0,0,0.6)';ctx.fillRect(0,0,c.width,c.height);
            ctx.fillStyle='#e94560';ctx.font='bold 28px sans-serif';ctx.textAlign='center';
            ctx.fillText('💥 游戏结束',c.width/2,c.height/2-25);
            ctx.fillStyle='#fff';ctx.font='20px sans-serif';ctx.fillText('得分: '+s.score,c.width/2,c.height/2+15);
            ctx.fillStyle='#0f3460';ctx.fillRect(c.width/2-70,c.height/2+40,140,45);
            ctx.fillStyle='#fff';ctx.font='16px sans-serif';ctx.fillText('重新开始',c.width/2,c.height/2+70);
        }
    }
    // Touch move
    addEventListener('touchmove',e=>{e.preventDefault();let t=e.touches[0];if(t&&s.started&&!s.over){player.x=t.clientX-20;player.y=Math.min(c.height-40,Math.max(0,t.clientY-20));}},{passive:false});
    addEventListener('click',()=>{if(!s.started||s.over){reset();s.started=true;}});
    addEventListener('mousemove',e=>{if(s.started&&!s.over){player.x=e.clientX-20;player.y=Math.min(c.height-40,Math.max(0,e.clientY-20));}});
    requestAnimationFrame(loop);
})();
</script>
</body>
</html>
    """.trimIndent()

    private fun getPlatformerTemplate(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
body{width:100vw;height:100vh;overflow:hidden;background:#1a1a2e;display:flex;justify-content:center;align-items:center;touch-action:none;}
canvas{display:block;}
</style>
</head>
<body>
<canvas id="game"></canvas>
<script>
(function(){
    'use strict';
    const c=document.getElementById('game'),ctx=c.getContext('2d');
    function r(){c.width=innerWidth;c.height=innerHeight;}r();addEventListener('resize',r);
    const GRAVITY=0.6,JUMP=-10,SPEED=4;
    let s={started:false,over:false,score:0};
    let p={x:50,y:100,w:30,h:40,vx:0,vy:0,onGround:false};
    let platforms=[{x:0,y:c.height-30,w:c.width,h:30},{x:200,y:c.height-120,w:120,h:15},{x:400,y:c.height-200,w:120,h:15},{x:100,y:c.height-300,w:100,h:15},{x:350,y:c.height-380,w:100,h:15}];
    let coins=[{x:240,y:c.height-150,x:20},{x:440,y:c.height-230,x:20},{x:140,y:c.height-330,x:20},{x:390,y:c.height-410,x:20}];
    let camera={x:0};
    function reset(){
        p={x:50,y:100,w:30,h:40,vx:0,vy:0,onGround:false};
        s={started:false,over:false,score:0};camera={x:0};
        coins=[{x:240,y:c.height-150},{x:440,y:c.height-230},{x:140,y:c.height-330},{x:390,y:c.height-410}];
    }
    function loop(){u();draw();requestAnimationFrame(loop);}
    function u(){
        if(!s.started||s.over)return;
        p.vy+=GRAVITY;p.x+=p.vx;p.y+=p.vy;p.onGround=false;
        platforms.forEach(pl=>{
            if(p.x+p.w>pl.x&&p.x<pl.x+pl.w&&p.y+p.h>pl.y&&p.y+p.h<pl.y+pl.h+8&&p.vy>0){p.y=pl.y-p.h;p.vy=0;p.onGround=true;}
        });
        if(p.y>c.height+100)s.over=true;
        camera.x=p.x-c.width/3;camera.x=Math.max(0,camera.x);
        coins=coins.filter(cn=>{if(p.x+p.w>cn.x+5&&p.x<cn.x+15&&p.y+p.h>cn.y&&p.y<cn.y+15){s.score+=10;return false;}return true;});
        if(coins.length==0)s.over=true;
    }
    function draw(){
        ctx.fillStyle='#1a1a2e';ctx.fillRect(0,0,c.width,c.height);
        ctx.save();ctx.translate(-camera.x,0);
        platforms.forEach(pl=>{ctx.fillStyle='#16213e';ctx.fillRect(pl.x,pl.y,pl.w,pl.h);ctx.fillStyle='#0f3460';ctx.fillRect(pl.x,pl.y,pl.w,3);});
        coins.forEach(cn=>{ctx.fillStyle='#ffd700';ctx.beginPath();ctx.arc(cn.x+10,cn.y+8,8,0,Math.PI*2);ctx.fill();ctx.fillStyle='#fff';ctx.beginPath();ctx.arc(cn.x+8,cn.y+5,3,0,Math.PI*2);ctx.fill();});
        ctx.fillStyle='#e94560';ctx.fillRect(p.x,p.y,p.w,p.h);
        ctx.fillStyle='#fff';ctx.fillRect(p.x+18,p.y+8,8,8);
        ctx.restore();
        ctx.fillStyle='#fff';ctx.font='16px sans-serif';ctx.textAlign='left';ctx.fillText('金币: '+s.score+'/'+coins.length,10,28);
        if(!s.started){
            ctx.fillStyle='rgba(0,0,0,0.5)';ctx.fillRect(0,0,c.width,c.height);
            ctx.fillStyle='#e94560';ctx.font='bold 28px sans-serif';ctx.textAlign='center';
            ctx.fillText('🏃 平台跳跃',c.width/2,c.height/3);
            ctx.fillRect(c.width/2-80,c.height/2-15,160,50);
            ctx.fillStyle='#fff';ctx.font='18px sans-serif';ctx.fillText('点击开始',c.width/2,c.height/2+12);
        }
        if(s.over){
            ctx.fillStyle='rgba(0,0,0,0.5)';ctx.fillRect(0,0,c.width,c.height);
            ctx.fillStyle='#fff';ctx.font='bold 26px sans-serif';ctx.textAlign='center';
            ctx.fillText(coins.length==0?'🎉 通关！':'💥 掉下去了',c.width/2,c.height/2-20);
            ctx.fillStyle='#e94560';ctx.fillRect(c.width/2-70,c.height/2+20,140,45);
            ctx.fillStyle='#fff';ctx.font='16px sans-serif';ctx.fillText('重新开始',c.width/2,c.height/2+50);
        }
    }
    document.addEventListener('keydown',e=>{
        if(!s.started&&e.code=='Space'){s.started=true;return;}
        if(s.over&&e.code=='Space'){reset();s.started=true;return;}
        if(e.code=='ArrowLeft')p.vx=-SPEED;if(e.code=='ArrowRight')p.vx=SPEED;
        if((e.code=='ArrowUp'||e.code=='Space')&&p.onGround)p.vy=JUMP;
    });
    document.addEventListener('keyup',e=>{if(e.code=='ArrowLeft'||e.code=='ArrowRight')p.vx=0;});
    // Touch
    let tx=0;
    addEventListener('touchstart',e=>{e.preventDefault();if(!s.started||s.over){s.started=true;reset();s.started=true;return;}tx=e.touches[0].clientX;if(p.onGround)p.vy=JUMP;});
    addEventListener('touchmove',e=>{e.preventDefault();let dx=e.touches[0].clientX-tx;if(Math.abs(dx)>20)p.vx=dx>0?SPEED:-SPEED;else p.vx=0;tx=e.touches[0].clientX;});
    addEventListener('touchend',()=>p.vx=0);
    requestAnimationFrame(loop);
})();
</script>
</body>
</html>
    """.trimIndent()

    private fun getPuzzleMatchTemplate(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
body{width:100vw;height:100vh;overflow:hidden;background:#1a1a2e;display:flex;justify-content:center;align-items:center;flex-direction:column;color:#fff;font-family:sans-serif;touch-action:none;text-align:center;}
</style>
</head>
<body>
<h1 style="font-size:22px;color:#e94560;margin:10px;">🧩 记忆配对</h1>
<p id="info" style="font-size:14px;color:#aaa;margin:5px;">翻开卡片找到所有配对</p>
<div id="grid" style="display:grid;grid-template-columns:repeat(4,70px);gap:8px;justify-content:center;margin:10px;"></div>
<button id="restart" style="margin:16px;padding:10px 30px;border:none;background:#e94560;color:#fff;border-radius:8px;font-size:16px;display:none;">重新开始</button>
<script>
const emojis=['🍎','🍊','🍋','🍇','🍓','🍒','🍑','🍌'];
let cards=[],flipped=[],matched=0,moves=0,locked=false;
function shuffle(a){for(let i=a.length-1;i>0;i--){let j=Math.floor(Math.random()*(i+1));[a[i],a[j]]=[a[j],a[i]];}return a;}
function init(){
    cards=shuffle([...emojis,...emojis]);flipped=[];matched=0;moves=0;locked=false;
    const grid=document.getElementById('grid');grid.innerHTML='';
    document.getElementById('restart').style.display='none';
    document.getElementById('info').textContent='翻开卡片找到所有配对 | 步数: 0';
    cards.forEach((emoji,i)=>{
        const div=document.createElement('div');
        div.style.cssText='width:70px;height:70px;border-radius:10px;display:flex;align-items:center;justify-content:center;font-size:28px;cursor:pointer;background:#0f3460;color:transparent;transition:all 0.3s;';
        div.dataset.index=i;
        div.addEventListener('click',()=>flip(i));
        grid.appendChild(div);
    });
}
function flip(i){
    if(locked||flipped.includes(i)||document.getElementById('grid').children[i].style.background!='rgb(15, 52, 96)')return;
    const el=document.getElementById('grid').children[i];
    el.style.background='#16213e';el.style.color='#fff';el.textContent=cards[i];
    flipped.push(i);
    if(flipped.length==2){
        moves++;locked=true;
        document.getElementById('info').textContent='翻开卡片找到所有配对 | 步数: '+moves;
        if(cards[flipped[0]]==cards[flipped[1]]){matched++;flipped=[];locked=false;if(matched==emojis.length){document.getElementById('info').textContent='🎉 恭喜完成！步数: '+moves;document.getElementById('restart').style.display='inline-block';}}
        else setTimeout(()=>{
            document.getElementById('grid').children[flipped[0]].style.background='#0f3460';document.getElementById('grid').children[flipped[0]].style.color='transparent';document.getElementById('grid').children[flipped[0]].textContent='';
            document.getElementById('grid').children[flipped[1]].style.background='#0f3460';document.getElementById('grid').children[flipped[1]].style.color='transparent';document.getElementById('grid').children[flipped[1]].textContent='';
            flipped=[];locked=false;
        },800);
    }
}
document.getElementById('restart').addEventListener('click',init);
init();
</script>
</body>
</html>
    """.trimIndent()

    private fun getShooterTemplate(): String = """
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no">
<style>
*{margin:0;padding:0;box-sizing:border-box;}
body{width:100vw;height:100vh;overflow:hidden;background:#0a0a1a;display:flex;justify-content:center;align-items:center;touch-action:none;}
canvas{display:block;}
</style>
</head>
<body>
<canvas id="game"></canvas>
<script>
(function(){
    'use strict';
    const c=document.getElementById('game'),ctx=c.getContext('2d');
    function r(){c.width=innerWidth;c.height=innerHeight;}r();addEventListener('resize',r);
    let s={started:false,over:false,score:0,level:1};
    let p={x:c.width/2,y:c.height-50,r:12};
    let bullets=[],enemies=[],frame=0;
    function reset(){s={started:false,over:false,score:0,level:1};p={x:c.width/2,y:c.height-50,r:12};bullets=[];enemies=[];frame=0;}
    function spawnWave(){let n=3+s.level;for(let i=0;i<n;i++){let ew=20+Math.random()*15;enemies.push({x:Math.random()*(c.width-40)+20,y:-30-20*i,w:ew,h:ew});}}
    function loop(){u();draw();requestAnimationFrame(loop);}
    function u(){
        if(!s.started||s.over)return;frame++;
        if(frame%10==0)bullets.push({x:p.x,y:p.y-10,w:3,h:12});
        if(frame%(60-Math.min(s.level*5,35))==0)spawnWave();
        bullets=bullets.filter(b=>{b.y-=8;return b.y>-12;});
        enemies=enemies.filter(e=>{e.y+=1.5+s.level*0.3;
            bullets=bullets.filter(b=>{if(b.x<e.x+e.w&&b.x+b.w>e.x&&b.y<e.y+e.h&&b.y+b.h>e.y){s.score+=5;return false;}return true;});
            let dx=p.x-(e.x+e.w/2),dy=p.y-(e.y+e.h/2);
            if(Math.sqrt(dx*dx+dy*dy)<p.r+Math.min(e.w,e.h)/2){s.over=true;}
            return e.y<c.height+20;
        });
        if(frame%300==0&&s.level<10)s.level++;
    }
    function draw(){
        ctx.fillStyle='#0a0a1a';ctx.fillRect(0,0,c.width,c.height);
        if(!s.started){
            ctx.fillStyle='#e94560';ctx.font='bold 28px sans-serif';ctx.textAlign='center';
            ctx.fillText('🎯 射击挑战',c.width/2,c.height/3);
            ctx.fillRect(c.width/2-80,c.height/2-15,160,50);
            ctx.fillStyle='#fff';ctx.font='18px sans-serif';ctx.fillText('点击开始',c.width/2,c.height/2+12);
            return;
        }
        ctx.fillStyle='#0f3460';ctx.beginPath();ctx.arc(p.x,p.y,p.r,0,Math.PI*2);ctx.fill();
        ctx.fillStyle='#ff0';bullets.forEach(b=>ctx.fillRect(b.x,b.y,b.w,b.h));
        ctx.fillStyle='#e94560';enemies.forEach(e=>ctx.fillRect(e.x,e.y,e.w,e.h));
        ctx.fillStyle='#fff';ctx.font='16px sans-serif';ctx.textAlign='left';ctx.fillText('分数: '+s.score+' | 波次: '+s.level,8,26);
        if(s.over){
            ctx.fillStyle='rgba(0,0,0,0.6)';ctx.fillRect(0,0,c.width,c.height);
            ctx.fillStyle='#e94560';ctx.font='bold 26px sans-serif';ctx.fillText('💀 游戏结束',c.width/2,c.height/2-20);
            ctx.fillStyle='#fff';ctx.font='18px sans-serif';ctx.fillText('得分: '+s.score,c.width/2,c.height/2+15);
            ctx.fillRect(c.width/2-60,c.height/2+35,120,40);
            ctx.fillStyle='#fff';ctx.font='14px sans-serif';ctx.fillText('重新开始',c.width/2,c.height/2+60);
        }
    }
    addEventListener('mousemove',e=>{p.x=e.clientX;p.y=Math.min(c.height-20,e.clientY);});
    addEventListener('touchmove',e=>{e.preventDefault();let t=e.touches[0];p.x=t.clientX;p.y=Math.min(c.height-20,t.clientY);});
    addEventListener('click',()=>{if(!s.started||s.over){reset();s.started=true;}});
    requestAnimationFrame(loop);
})();
</script>
</body>
</html>
    """.trimIndent()
}
