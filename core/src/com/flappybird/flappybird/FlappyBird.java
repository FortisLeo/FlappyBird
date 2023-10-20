package com.flappybird.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
    SpriteBatch batch;
    ShapeRenderer shapeRenderer;
    Texture background, topTube, bottomTube, gameOver, play;
    int flapState = 0, gameState = 0, numberOfTubes = 4, score = 0, scoringTube = 0;
    float birdY = 0, velocity = 0, gravity = 2, gap = 400, maxTubeOffset, tubeVelocity = 4, distance;
    float screenWidth, screenHeight;
    float[] tubeX = new float[numberOfTubes], tubeOffset = new float[numberOfTubes];
    Random randomGenerator;
    Circle birdCircle;
	BitmapFont font, font2;
    Rectangle[] topTubeRectangle, bottomTubeRectangle;
    Texture[] birds;

    @Override
    public void create() {
        batch = new SpriteBatch();
        background = new Texture("bg.png");
		gameOver = new Texture("gameover.png");
        birds = new Texture[2];
        play = new Texture("play.png");
        birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");

        // shapeRenderer = new ShapeRenderer();
        birdCircle = new Circle();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
        font2 = new BitmapFont();
		font2.setColor(Color.WHITE);
		font2.getData().setScale(7);
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        topTubeRectangle = new Rectangle[numberOfTubes];
        bottomTubeRectangle = new Rectangle[numberOfTubes];
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        maxTubeOffset = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();

        distance = Gdx.graphics.getWidth() * 0.6f;
		startGame();




    }

	public void startGame(){
		birdY = Gdx.graphics.getHeight() / 2 - birds[flapState].getHeight() / 2;
		for (int i = 0; i < numberOfTubes; i++) {
			tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (screenHeight - gap);
			tubeOffset[i] = (tubeOffset[i] > 380.0f) ? 292.34f : (tubeOffset[i] < -380.0f) ? -292.34f : tubeOffset[i];
			System.out.println(tubeOffset[i]);
			tubeX[i] = screenWidth/ 2 - topTube.getWidth() / 2 + screenWidth + i * distance;
            velocity = 0;
			topTubeRectangle[i] = new Rectangle();
			bottomTubeRectangle[i] = new Rectangle();

		}
	}

    @Override
    public void render() {

        batch.begin();
        batch.draw(background, 0, 0, screenWidth, screenHeight);

        if (gameState == 1) {

            if (tubeX[scoringTube] < Gdx.graphics.getWidth() / 2) {
                Gdx.app.log("score", String.valueOf(score));
                score++;
                if (scoringTube < numberOfTubes - 1) {
                    scoringTube++;
                } else {
                    scoringTube = 0;
                }
				if (score%5==0){
					tubeVelocity+=2;
				}
            }
            if (Gdx.input.justTouched()) {
                velocity = -30;

            }

            for (int i = 0; i < numberOfTubes; i++) {
                if (tubeX[i] < -topTube.getWidth()) {
                    tubeX[i] += numberOfTubes * distance;
                    tubeOffset[i] = (randomGenerator.nextFloat() - 0.5f) * (screenHeight - gap);
                    tubeOffset[i] = (tubeOffset[i] > 380.0f) ? 292.34f : (tubeOffset[i] < -380.0f) ? -292.34f : tubeOffset[i];
                } else {
                    tubeX[i] = tubeX[i] - tubeVelocity;

                }

                batch.draw(topTube, tubeX[i], screenHeight/ 2 + gap / 2 + tubeOffset[i]);
                batch.draw(bottomTube, tubeX[i], screenHeight / 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i]);

                topTubeRectangle[i] = new Rectangle(tubeX[i], screenHeight / 2 + gap / 2 + tubeOffset[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangle[i] = new Rectangle(tubeX[i], screenHeight/ 2 - gap / 2 - bottomTube.getHeight() + tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());


            }


            if (birdY > 0 ) {
                velocity = velocity + gravity;
                birdY -= velocity;
            } else {
				gameState = 2; // 2 is the gameover state
			}


        } else if (gameState ==0) {
			if (Gdx.input.justTouched()) {
				gameState =1;

			}
			
		} else if (gameState ==2) {
			batch.draw(gameOver, screenWidth/2 - gameOver.getWidth()/2, screenHeight/2 );
            String restartText = "Score: " + String.valueOf(score);
            GlyphLayout layout = new GlyphLayout(font2, restartText);
            float textX = (screenWidth - layout.width) / 2;
            float textY = screenHeight / 2 - 50;
            batch.setProjectionMatrix(batch.getProjectionMatrix());
            font2.draw(batch, layout, textX, textY);
            batch.draw(play, screenWidth/2 - play.getWidth()/2, screenHeight/2 - gameOver.getHeight() - play.getHeight()- 50);
            if (Gdx.input.justTouched()) {
				gameState =1;
				startGame();
				score =0;
				scoringTube = 0;

			}

		}

        if (flapState == 0) {
            flapState = 1;
        } else {
            flapState = 0;
        }

        batch.draw(birds[flapState], screenWidth / 2 - birds[flapState].getWidth() / 2, birdY);
		font.draw(batch, String.valueOf(score), 100, 200);
        batch.end();
        birdCircle.set(screenWidth / 2, birdY + birds[flapState].getHeight() / 2, birds[flapState].getWidth() / 2);
//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.RED);
//		shapeRenderer.circle(birdCircle.x, birdCircle.y ,birdCircle.radius);
        for (int i = 0; i < numberOfTubes; i++) {
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight()/2+gap/2+tubeOffset[i], topTube.getWidth(), topTube.getHeight());
//			shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight()/2-gap/2-bottomTube.getHeight()+tubeOffset[i], bottomTube.getWidth(), bottomTube.getHeight());

            if (Intersector.overlaps(birdCircle, topTubeRectangle[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangle[i])) {
                Gdx.app.log("collision", "yes");
//				gameState = 2;
            }

        }
//		shapeRenderer.end();


    }

}
