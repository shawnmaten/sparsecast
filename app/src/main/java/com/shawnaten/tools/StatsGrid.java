package com.shawnaten.tools;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shawnaten.simpleweather.R;

import java.util.ArrayList;

public class StatsGrid {
    /*int colors[] = new int[]{0xFFF8BBD0, 0xFFF06292, 0xFFD81B60, 0xFFF48FB1, 0xFFE91E63,
            0xFFAD1457, 0xFFEC407A, 0xFFC2185B, 0xFF880E4F};*/

    /*
    public static final int colors[] = new int[]{
            0xFFB2EBF2,
            0xFF4DD0E1,
            0xFF00ACC1,
            0xFF80DEEA,
            0xFF00BCD4,
            0xFF00838F,
            0xFF26C6DA,
            0xFF0097A7,
            0xFF006064
    };

    public static final int textColors[] = new int[]{
            R.color.text_primary,
            R.color.text_primary,
            R.color.text_primary,
            R.color.text_primary,
            R.color.text_primary,
            R.color.text_primary_light,
            R.color.text_primary,
            R.color.text_primary_light,
            R.color.text_primary_light
    };
    */

    public static void configureGrid(
            Activity activity, ArrayList<Integer> labels, ArrayList<String> values,
            ArrayList<String> units, LinearLayout grid) {

        LinearLayout column1 = (LinearLayout) grid.findViewById(R.id.column_1);
        LinearLayout column2 = (LinearLayout) grid.findViewById(R.id.column_2);

        column1.removeAllViews();
        column2.removeAllViews();

        for (int i = 0; i < labels.size(); i++) {
            LinearLayout column;

            if (i % 2 == 0)
                column = column1;
            else
                column = column2;

            View card = activity.getLayoutInflater().inflate(R.layout.stat_card, column, false);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                    card.getLayoutParams();
            int cardByCardMargin = activity.getResources()
                    .getDimensionPixelSize(R.dimen.card_by_card_margin);

            if (i % 2 == 0)
                layoutParams.setMarginEnd(cardByCardMargin);
            else
                layoutParams.setMarginStart(cardByCardMargin);

            TextView titleView = (TextView) card.findViewById(R.id.title);
            TextView valueView = (TextView) card.findViewById(R.id.value);
            TextView unitsView = (TextView) card.findViewById(R.id.units);

            titleView.setText(activity.getString(labels.get(i)));
            valueView.setText(values.get(i));
            unitsView.setText(units.get(i));

            card.setLayoutParams(layoutParams);

            if (i % 2 == 0) {
                column1.addView(card);
            } else {
                column2.addView(card);
            }
        }

        /*
        int height = (int) (context.getResources().getDisplayMetrics().widthPixels
                - (32 * context.getResources().getDisplayMetrics().density));
        height /= 3;

        for (int i = 0; i < grid.getChildCount(); i++) {
            LinearLayout row = (LinearLayout) grid.getChildAt(i);
            for (int j = 0; j < row.getChildCount(); j++) {
                if (Space.class.isInstance(row.getChildAt(j)))
                    break;
                View item = row.getChildAt(j);

                ViewGroup.LayoutParams layoutParams = item.getLayoutParams();
                layoutParams.height = height;
                item.setLayoutParams(layoutParams);
                item.setBackgroundColor(colors[j + (i * 3)]);

                TextView label, value, unit;
                label = (TextView) item.findViewById(R.placeId.label);
                value = (TextView) item.findViewById(R.placeId.value);
                unit = (TextView) item.findViewById(R.placeId.units);

                label.setText(context.getResources().getString(labels.get(j + (i * 3))));
                label.setTextColor(context.getResources().getColor(textColors[j + (i * 3)]));

                value.setText(values.get(j + (i * 3)));
                value.setTextSize(valueTextSizes.get(j + (i * 3)));
                value.setTextColor(context.getResources().getColor(textColors[j + (i * 3)]));

                if (units.get(j + (i * 3)) != null)
                    unit.setText(units.get(j + (i * 3)));
                else
                    unit.setVisibility(View.GONE);

                unit.setTextColor(context.getResources().getColor(textColors[j + (i * 3)]
                        == R.color.text_primary ? R.color.text_secondary
                        : R.color.text_secondary_light));
            }
        }
        */
    }
}
