package com.davidlacho.interactivestory.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.davidlacho.interactivestory.R;
import com.davidlacho.interactivestory.model.Page;
import com.davidlacho.interactivestory.model.Story;
import java.util.Stack;

public class StoryActivity extends AppCompatActivity {

  public static final String TAG = StoryActivity.class.getSimpleName();

  private Story story;
  private String name;
  private ImageView storyImageView;
  private TextView storyTextView;
  private Button choice1Button;
  private Button choice2Button;
  private Stack<Integer> pageStack = new Stack<Integer>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_story);

    storyImageView = findViewById(R.id.storyImageView);
    storyTextView = findViewById(R.id.storyTextView);
    choice1Button = findViewById(R.id.choice1Button);
    choice2Button = findViewById(R.id.choice2Button);

    Intent intent = getIntent();

    name = intent.getStringExtra(getString(R.string.key_name));

    if (name == null || name.isEmpty()) {
      name = "Friend";
    }

    Log.d(TAG, "name: " + name);
    story = new Story();

    loadPage(0);

  }

  private void loadPage(int pageNumber) {
    pageStack.push(pageNumber);

    choice1Button.setVisibility(View.VISIBLE);
    choice2Button.setVisibility(View.VISIBLE);
    final Page page = story.getPage(pageNumber);
    Drawable image = ContextCompat.getDrawable(this, page.getImageId());
    storyImageView.setImageDrawable(image);
    String pageText = getString(page.getTextId());
//    Add name if placeholder included. Won't add if not
    pageText = String.format(pageText, name);
    storyTextView.setText(pageText);

    if (page.isFinalPage()) {
      choice1Button.setVisibility(View.INVISIBLE);
      choice2Button.setText(getString(R.string.play_again_button_text));
      choice2Button.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          loadPage(0);
        }
      });
    } else {
      loadButtons(page);
    }
  }

  private void loadButtons(final Page page) {
    choice1Button.setText(page.getChoice1().getTextId());

    choice1Button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        int nextPage = page.getChoice1().getNextPage();
        loadPage(nextPage);
      }
    });
    choice2Button.setText(page.getChoice2().getTextId());
    choice2Button.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        int nextPage = page.getChoice2().getNextPage();
        loadPage(nextPage);
      }
    });
  }

  @Override
  public void onBackPressed() {
    pageStack.pop();
    if (pageStack.isEmpty()) {
      super.onBackPressed();
    } else {
//      This second call to pop will be pushed right back on because it will be loaded back on the page.
      loadPage(pageStack.pop());
    }
  }
}
